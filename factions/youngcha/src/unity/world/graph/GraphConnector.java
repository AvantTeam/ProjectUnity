package unity.world.graph;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

import static arc.math.geom.Geometry.*;

/*
 * This class acts as the thing that'll connect to the graph network as a vertex.
 * It handles connecting to adjacent blocks (and thus finding edges) as well.
 * */
public abstract class GraphConnector<T extends Graph<T>>{
    public final int id = idAccum++;
    private static int idAccum = 0;
    GraphNode<T> node;
    T graph;
    float minDistance, maxDistance; //only used for distance
    public OrderedSet<GraphEdge<T>> connections = new OrderedSet<>();
    boolean disconnectWhenRotate = true;


    public GraphConnector(GraphNode<T> node, T graph){
        this.node = node;
        graph.addVertex(this);
    }

    public void update(){
    }

    public void onProximityUpdate(){

    }

    public abstract void recalcPorts();

    public abstract void recalcNeighbours();

    public abstract boolean canConnect(Point2 pt, GraphConnector<T> conn);

    public abstract GraphEdge<T> tryConnect(Point2 pt, GraphConnector<T> conn);

    public boolean isConnected(GraphConnector<T> t){
        for(var edge : connections){
            if(edge.other(this) == t){
                return true;
            }
        }
        return false;
    }

    public void eachConnected(Cons<GraphConnector<T>> cons){
        for(var edge : connections){
            cons.get(edge.other(this));
        }
    }

    public boolean isConnected(GraphBuild t){
        for(var edge : connections){
            if(edge.other(this).node.build == t){
                return true;
            }
        }
        return false;
    }

    public void disconnect(){
        graph.removeVertex(this);
        if(connections.size > 0){
            Log.info("[scarlet] disconnected vertex still has edges!");
        }
    }

    public void removeEdge(GraphEdge<T> ge){
        if(connections.remove(ge)){
            ge.valid = false;
            triggerConnectionChanged();
        }
    }

    public void triggerConnectionChanged(){
        this.node.build.onConnectionChanged(this);
    }

    public void write(Writes write){}

    public void read(Reads read){}

    public GraphEdge<T> addEdge(GraphConnector<T> extConn){
        long edgeId = GraphEdge.getId(this, extConn);
        if(graph.edges.containsKey(edgeId)){
            if(!connections.contains(graph.edges.get(edgeId))){
                var edge = graph.edges.get(edgeId);
                connections.add(edge);
                edge.valid = true; // in case.
            }
            return graph.edges.get(edgeId);
        }
        var edge = new GraphEdge<>(this, extConn);
        graph.addEdge(edge);
        connections.add(edge);
        triggerConnectionChanged();
        return edge;
    }


    ///derivative classes
    //single distance connections?
    public static class DistanceGraphConnector<U extends Graph<U>> extends GraphConnector<U>{
        public int maxConnections = 1;
        public Point2[] connection; // connection?
        int validConnections = 0;

        public DistanceGraphConnector(GraphNode<U> node, U graph){
            super(node, graph);
            connection = new Point2[maxConnections];
            disconnectWhenRotate = false;
        }

        public DistanceGraphConnector(int connections, GraphNode<U> node, U graph){
            super(node, graph);
            maxConnections = connections;
            connection = new Point2[maxConnections];
            disconnectWhenRotate = false;
        }

        public Point2 first(){
            for(var p2 : connection){
                if(p2 == null || (p2.x == 0 && p2.y == 0)){
                    continue;
                }
                return p2;
            }
            return null;
        }

        public void refreshValidConnections(){
            validConnections = 0;
            for(var p2 : connection){
                if(p2 == null || (p2.x == 0 && p2.y == 0)){
                    continue;
                }
                validConnections++;
            }
        }

        public int validConnections(){
            return validConnections;
        }

        public void resize(int size){
            maxConnections = size;
            Point2[] newConnection = new Point2[maxConnections];
            System.arraycopy(connection, 0, newConnection, 0, Math.min(connection.length, size));
            connection = newConnection;
            refreshValidConnections();
        }

        public void connectTo(DistanceGraphConnector<U> other){
            Tile ext = other.node.build().tile;
            Tile cur = node.build().tile;

            var edge = other.tryConnect(new Point2(cur.x - ext.x, cur.y - ext.y), this);
            if(edge != null){
                if(!connections.contains(edge)){
                    connections.add(edge);
                }
                addConnection(other);
            }

        }

        public void connectTo(int rx, int ry){
            Tile intrl = node.build().tile;
            Building build = Vars.world.build(intrl.x + rx, intrl.y + ry);
            if(build == null){
                return;
            }
            if(build instanceof GraphBuild graphBuild){
                GraphNode<U> extNode = graphBuild.getGraphNode(graph.getClass());
                if(extNode == null){
                    return;
                }
                for(var extConnector : extNode.connector){
                    if(!(extConnector instanceof GraphConnector.DistanceGraphConnector<U> distConn)){
                        continue;
                    }
                    var edge = distConn.tryConnect(new Point2(-rx, -ry), this);
                    if(edge != null){
                        if(!connections.contains(edge)){
                            connections.add(edge);
                        }
                        addConnection(distConn);
                    }

                }
            }
        }

        @Override
        public void recalcPorts(){
            //doesn't need to
        }

        @Override
        public void recalcNeighbours(){
            connections.clear();
            for(var p2 : connection){
                if(p2 == null || (p2.x == 0 && p2.y == 0)){
                    continue;
                }
                connectTo(p2.x, p2.y);
            }
            refreshValidConnections();
        }

        boolean addConnection(DistanceGraphConnector<U> other){
            Tile ext = other.node.build().tile;
            Tile cur = node.build().tile;
            Point2 relPos = new Point2(ext.x - cur.x, ext.y - cur.y);
            for(Point2 point2 : connection){
                if(point2 != null && (point2.x == relPos.x && point2.y == relPos.y)){
                    return true; // it exists already!
                }
            }
            for(int i = 0; i < connection.length; i++){
                if(connection[i] == null || (connection[i].x == 0 && connection[i].y == 0)){
                    connection[i] = relPos;
                    refreshValidConnections();
                    return true;
                }
            }
            return false;
        }

        public void disconnectTo(DistanceGraphConnector<U> other){
            GraphEdge<U> toRemove = null;
            for(var edge : connections){
                if(edge.other(this) == other){
                    toRemove = edge;
                    break;
                }
            }
            if(toRemove == null){
                return;
            }
            Log.info("disconnecting edge." + toRemove);
            removeConnection(other);
            other.removeConnection(this);
            graph.removeEdge(toRemove);
        }

        @Override
        public void disconnect(){
            Log.info("disconnecting.");
            while(!connections.isEmpty()){
                disconnectTo((DistanceGraphConnector<U>)connections.first().other(this));
            }
            super.disconnect();
        }

        void removeConnection(DistanceGraphConnector<U> other){
            Tile ext = other.node.build().tile;
            Tile cur = node.build().tile;
            Point2 relpos = new Point2(ext.x - cur.x, ext.y - cur.y);
            for(int i = 0; i < connection.length; i++){
                Point2 point2 = connection[i];
                if(point2 != null && (point2.x == relpos.x && point2.y == relpos.y)){
                    connection[i] = null;
                    refreshValidConnections();
                    Log.info("disconnected from:" + point2);
                    return;
                }
            }
        }

        @Override
        public boolean canConnect(Point2 pt, GraphConnector<U> conn){
            return false;
        }

        @Override
        public GraphEdge<U> tryConnect(Point2 pt, GraphConnector<U> extConn){
            if(addConnection((DistanceGraphConnector<U>)extConn)){
                return addEdge(extConn);
            }
            return null;
        }

        public void write(Writes write){
            for(int i = 0; i < maxConnections; i++){
                write.i(connection[i] == null ? 0 : connection[i].pack());
            }
        }

        public void read(Reads read){
            for(int i = 0; i < maxConnections; i++){
                connection[i] = Point2.unpack(read.i());
            }
            refreshValidConnections();
        }
    }

    //connections in fixed locations like at the ends of a block
    public static class FixedGraphConnector<U extends Graph<U>> extends GraphConnector<U>{
        int[] connectionPointIndexes;
        public ConnectionPort<U>[] connectionPoints;

        public FixedGraphConnector(GraphNode<U> node, U graph, int... connections){
            super(node, graph);
            connectionPointIndexes = connections;
        }

        @Override
        public void recalcPorts(){
            if(connections.size > 0){
                throw new IllegalStateException("graph connector must have no connections before port recalc");
            }
            connectionPoints = surfaceConnectionsOf(this, connectionPointIndexes);
        }

        @Override
        public void recalcNeighbours(){
            if(connectionPoints == null){
                recalcPorts();
            }
            //disconnect?
            if(connections.size > 0){
                disconnect();
            }
            for(var edge : connections){
                if(edge.valid){
                    edge.valid = false;
                    Log.info("Deleted valid edge, this may cause issues.");
                }
            }
            connections.clear();

            //clear edges from graph as well?

            Tile intrl = node.build().tile;
            Point2 temp = new Point2();
            for(var cp : connectionPoints){
                //for each connection point get the relevant tile it connects to. If it's a connection point, then attempt a connection.
                temp.set(intrl.x, intrl.y).add(cp.relPos).add(cp.dir);
                Building building = Vars.world.build(temp.x, temp.y);
                if(building instanceof GraphBuild igraph){
                    GraphNode<U> extNode = igraph.getGraphNode(graph.getClass());
                    if(extNode == null){
                        continue;
                    }
                    for(var extConnector : extNode.connector){
                        if(!(extConnector instanceof FixedGraphConnector)){
                            continue;
                        }
                        var edge = extConnector.tryConnect(cp.relPos.cpy().add(cp.dir), this);
                        if(edge != null){
                            cp.edge = edge;
                            if(!connections.contains(edge)){
                                connections.add(edge);
                            }
                        }

                    }
                }
                if(cp.edge != null && !cp.edge.valid){
                    cp.edge = null;
                }
            }
            triggerConnectionChanged();
        }

        public ConnectionPort<U>[] surfaceConnectionsOf(GraphConnector<U> gc, int[] connectIds){
            Seq<ConnectionPort<U>> ports = new Seq<>(connectIds.length);
            for(int i = 0; i < connectIds.length; i++){
                if(connectIds[i] == 0){
                    continue;
                }
                var port = getConnectSidePos(i, gc.node.block().size, gc.node.build().rotation);
                port.ordinal = ports.size;
                ports.add(port);
            }
            return ports.toArray(ConnectionPort.class);
        }

        //this came from js, but im not sure if it's relative to the center or the bl corner of the building.
        //get positions along the sides.
        public ConnectionPort<U> getConnectSidePos(int index, int size, int rotation){
            int side = index / size;
            side = (side + rotation) % 4;
            Point2 tangent = d4((side + 1) % 4);
            int originX = 0, originY = 0;
            if(size > 1){
                originX += size / 2;
                originY += size / 2;
                originY -= size - 1;
                if(side > 0){
                    for(int i = 1; i <= side; i++){
                        originX += d4x(i) * (size - 1);
                        originY += d4y(i) * (size - 1);
                    }
                }
                originX += tangent.x * (index % size);
                originY += tangent.y * (index % size);
            }
            var c = new ConnectionPort<>(this, new Point2(originX, originY), new Point2(d4x(side), d4y(side)));

            c.index = index;
            return c;
        }

        public ConnectionPort<U> isConnectionPortHere(Point2 worldPos){
            if(connectionPoints == null){
                recalcPorts();
            }
            Tile intrl = node.build().tile;
            Point2 pt = (worldPos).cpy();
            pt.sub(intrl.x, intrl.y);
            for(var cp : connectionPoints){
                if(pt.equals(cp.relPos.x, cp.relPos.y)){
                    return cp;
                }
            }
            return null;
        }

        public ConnectionPort<U> areConnectionPortsConnectedTo(Point2 worldPortPos, Building building){
            if(connectionPoints == null){
                recalcPorts();
            }
            Tile intrl = node.build().tile;
            Point2 pt = (worldPortPos).cpy();
            pt.sub(intrl.x, intrl.y);
            for(var cp : connectionPoints){
                if(pt.equals(cp.relPos.x, cp.relPos.y) && cp.connectedToTile().build == building){
                    return cp;
                }
            }
            return null;
        }

        public boolean canConnect(Point2 pt, GraphConnector<U> conn){
            // Point2 pt =(external.relPos.cpy()).add(external.dir);
            Tile ext = conn.node.build().tile;
            pt.add(ext.x, ext.y);
            return areConnectionPortsConnectedTo(pt, conn.node.build()) != null;
        }


        @Override
        public GraphEdge<U> tryConnect(Point2 pt, GraphConnector<U> extConn){
            Tile ext = extConn.node.build().tile;
            pt.add(ext.x, ext.y);
            var port = areConnectionPortsConnectedTo(pt, extConn.node.build());
            if(port == null){
                return null;
            }
            var edge = addEdge(extConn);
            if(edge != null){
                port.edge = edge;
            }
            return edge;
        }

        @Override
        public void removeEdge(GraphEdge<U> ge){
            super.removeEdge(ge);
            if(connectionPoints == null){
                return; //how this occurs I don't know
            }
            for(var cp : connectionPoints){
                if(cp.edge != null && !cp.edge.valid){
                    cp.edge = null;
                }
            }
        }

        public void eachConnected(Cons2<GraphConnector<U>, ConnectionPort<U>> cons){
            if(connectionPoints == null){
                return;
            }
            for(var port : connectionPoints){
                if(port.edge != null){
                    cons.get(port.edge.other(this), port);
                }
            }
        }

        public static class ConnectionPort<U extends Graph<U>>{
            Point2 relPos;// position of attachment within the block
            Point2 dir; //if 0,0, is universal direction connector (?? though 6 months later IDK if I want to do that)
            boolean occupied = false;
            GraphConnector<U> connector;
            int index = -1;
            int ordinal = 0;
            public GraphEdge<U> edge = null;

            public ConnectionPort(GraphConnector<U> connector, Point2 relPos, Point2 dir){
                this.relPos = relPos;
                this.dir = dir;
                this.connector = connector;
            }

            public int getIndex(){
                return index;
            }

            public Point2 getDir(){
                return dir;
            }

            public Point2 getRelPos(){
                return relPos;
            }

            public Tile connectedToTile(){
                return Vars.world.tile(connector.node.build().tile.x + relPos.x + dir.x, connector.node.build().tile.y + relPos.y + dir.y);
            }

            public int getOrdinal(){
                return ordinal;
            }
        }
    }


    @Override
    public String toString(){
        return "GraphConnector{" +
        "id=" + id +
        ", node=" + node.build().block +
        '}';
    }

    public T getGraph(){
        return graph;
    }

    public GraphNode<T> getNode(){
        return node;
    }
}
