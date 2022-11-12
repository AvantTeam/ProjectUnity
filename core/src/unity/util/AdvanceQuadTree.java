package unity.util;

import arc.math.geom.*;
import arc.struct.*;
import unity.util.AdvanceQuadTree.*;

import java.util.*;

public class AdvanceQuadTree<T extends AdvanceQuadTreeObject<T>> extends QuadTree<T>{
    public AdvanceQuadTree(Rect bounds){
        super(bounds);
    }

    @Override
    protected void split(){
        if(!leaf) return;

        float subW = bounds.width / 2;
        float subH = bounds.height / 2;

        if(botLeft == null){
            botLeft = newChild(new Rect(bounds.x, bounds.y, subW, subH));
            botRight = newChild(new Rect(bounds.x + subW, bounds.y, subW, subH));
            topLeft = newChild(new Rect(bounds.x, bounds.y + subH, subW, subH));
            topRight = newChild(new Rect(bounds.x + subW, bounds.y + subH, subW, subH));
        }
        leaf = false;

        for(Iterator<T> iterator = objects.iterator(); iterator.hasNext();){
            T obj = iterator.next();
            hitbox(obj);
            AdvanceQuadTree<T> child = (AdvanceQuadTree<T>)getFittingChild(tmp);
            if(child != null){
                //obj.tree = child;
                child.insert(obj);
                iterator.remove();
            }
        }
    }

    @Override
    protected void unsplit(){
        if(leaf) return;
        moveClear(botLeft.objects);
        moveClear(botRight.objects);
        moveClear(topLeft.objects);
        moveClear(topRight.objects);
        botLeft.clear();
        botRight.clear();
        topLeft.clear();
        topRight.clear();
        leaf = true;
    }

    @SuppressWarnings("unchecked")
    void moveClear(Seq<T> seq){
        Object[] items = seq.items;
        for(int i = 0, n = seq.size; i < n; i++){
            ((T)items[i]).tree = this;
            items[i] = null;
            objects.add((T)items[i]);
        }
        seq.size = 0;
    }

    @Override
    public void insert(T obj){
        hitbox(obj);
        if(!bounds.overlaps(tmp)){
            return;
        }

        totalObjects++;

        if(leaf && objects.size + 1 > maxObjectsPerNode) split();

        if(leaf){
            objects.add(obj);
            obj.tree = this;
        }else{
            hitbox(obj);
            QuadTree<T> child = getFittingChild(tmp);
            if(child != null){
                child.insert(obj);
            }else{
                objects.add(obj);
                obj.tree = this;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clear(){
        Object[] items = objects.items;
        for(int i = 0, n = objects.size; i < n; i++){
            ((T)items[i]).tree = null;
            items[i] = null;
        }
        objects.size = 0;

        totalObjects = 0;
        if(!leaf){
            topLeft.clear();
            topRight.clear();
            botLeft.clear();
            botRight.clear();
        }
        leaf = true;
    }

    @Override
    protected QuadTree<T> newChild(Rect rect){
        return new AdvanceQuadTree<>(rect);
    }

    public static abstract class AdvanceQuadTreeObject<T extends AdvanceQuadTreeObject<T>> implements QuadTreeObject{
        public AdvanceQuadTree<T> tree;
    }
}
