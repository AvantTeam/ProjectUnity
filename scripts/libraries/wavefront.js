//Wavefront Object converter/library made for Mindustry, Made be Eye Of Darkness.
//Broken
//textures, parameter space vertices, line elements, vertex normals and smoothing currently unsupported.
const defaultScl = Vars.tilesize / 2;

//const tempVecc = new Vec3();
const tempColor = new Color();

const fltConv = val => new java.lang.Float(val);
const intConv = val => new java.lang.Integer(val);
//faces: Seq(int[]). vertices: Seq(Vec3);
function Obj(faces, vertices){
	this.faces = faces;
	this.moddedvertices = new Seq();
	this.vertices = vertices;
	//this.uvs = null;
	this.region = null;
	//this.textureA = null;
	
	this.load = () => {
		this.region = Core.atlas.white();
		//this.textureA = new Texture(this.region.texture.getTextureData());
		//this.region = Core.atlas.find("error");
		//print("Texture Loaded OBJ");
		/*try{
			print(this.region.texture.getClass());
			print(this.region.texture.getTextureData().getClass());
		}catch(e){
			print(e);
		};*/
	};
	var ss = 0;
	
	this.vertices.each(v => {
		ss = Math.max(ss, Math.abs(v.z));
		this.moddedvertices.add(v.cpy());
	});
	
	this.shadingStrength = Math.max(ss, 1);
	
	this.draw = (x, y, scl, rx, ry, rz) => {
		var indx = 0;
		this.vertices.each(v => {
			var vecC = this.moddedvertices.get(indx);
			vecC.set(v);
			
			vecC.rotate(Vec3.X, rx);
			vecC.rotate(Vec3.Y, ry);
			vecC.rotate(Vec3.Z, rz);
			
			vecC.add(x, y, 0);
			
			indx++;
		});
		
		var tz = Draw.z();
		
		this.faces.each(f => {
			//var faceP = [];
			var faceP = new FloatSeq();
			var z = 0;
			var total = 1;
			
			var originalColor = Draw.getColor().cpy();
			var packedMixedCol = Draw.getMixColor().toFloatBits();
			
			for(var s = 0; s < f.length; s++){
				var vert = this.moddedvertices.get(f[s]);
				z += vert.z;
				total++;
			};
			z /= total;
			
			for(var i = 0; i < f.length; i++){
				var cx = fltConv(vert.x * defaultScl * scl);
				var cy = fltConv(vert.y * defaultScl * scl);
				var vert = this.moddedvertices.get(f[i]);
				
				//faceP.push(cx);
				//faceP.push(cy);
				faceP.add(cx);
				faceP.add(cy);
				
				tempColor.set(originalColor).mul(fltConv(z / this.shadingStrength));
				
				//faceP.push(tempColor.toFloatBits());
				faceP.add(fltConv(tempColor.toFloatBits()));
				
				faceP.add(fltConv(this.region.u));
				faceP.add(fltConv(this.region.v));
				
				faceP.add(fltConv(packedMixedCol));
				
				//faceP.push(this.region.u);
				//faceP.push(this.region.v);
				
				//faceP.push(packedMixedCol);
			};
			
			Draw.z(tz + (z / 1000));
			//var tmp = new Seq(faceP);
			//faceP = tmp.toArray(java.lang.Float);
			faceP = faceP.toArray();
			//TODO fix ArrayIndexOutOfBoundsException
			try{
				//print(faceP);
				//Draw.vert(this.region.texture, faceP, 0, faceP.length);
				Draw.vert(this.region.texture, faceP, 0, faceP.length);
				//Draw.vert(this.textureA, faceP, 0, faceP.length);
			}catch(e){
				print(e);
			};
		});
		Draw.z(tz);
	};
};

//should be able to function with different .obj formatting types.
const converter = (directory) => {
	var file = readString("assets/" + directory);
	var separatedFile = file.split("\n");
	
	var indx = 0;
	//var vertices = new Seq();
	var vertArray = [];
	var faces = new Seq();
	for(var i = 0; i < separatedFile.length; i++){
		if(separatedFile[i].lastIndexOf("v ") != -1){
			var val = separatedFile[i].slice(2, separatedFile[i].length);
			var separatedA = val.split(" ");
			var x = parseFloat(separatedA[0]);
			var y = parseFloat(separatedA[2]);
			var z = parseFloat(separatedA[1]);
			
			vertArray[indx] = new Vec3(x, y, z);
			//vertices.set(indx, new Vec3(x, y, z));
			indx++;
		};
		if(separatedFile[i].lastIndexOf("f ") != -1){
			var val = separatedFile[i].slice(2, separatedFile[i].length);
			var faceVal = val.split(" ");
			var faceArray = [];
			for(var k = 0; k < faceVal.length; k++){
				var faceType = faceVal[k].split("/");
				
				faceArray[k] = parseInt(faceType[0]) - 1;
			};
			//print(faceArray + ": F");
			faces.add(faceArray);
		};
	};
	var vertices = new Seq(vertArray);
	
	//print(vertices);
	
	return new Obj(faces, vertices);
};

//const testL = converter("star.obj");

module.exports = {
	object(directory){
		return converter(directory);
	}
};