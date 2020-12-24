function look(a, b){
	var aCheck = a * 90 + 180;

	if(a >= 360){
		a += -360;
	}

	return aCheck == (b * 90);
}

const mechConv = extendContent(Conveyor, "mechanical-conveyor", {
	load(){
		this.super$load();
		this.shadowReg = Core.atlas.find(this.name + "-shadow");
	}
});

mechConv.buildType = () => {
	return extendContent(Conveyor.ConveyorBuild, mechConv, {
		draw(){
			this.super$draw();

            Draw.z(Layer.block);

            if(this.front() == null || (this.front() != null && this.front().block.name != mechConv.name)){
				Draw.rect(mechConv.shadowReg, this.x, this.y, (this.rotation * 90));
			}

			var back = (this.back() != null) ? this.back().tile : this.tile;
			var left = (this.left() != null) ? this.left().tile : this.tile;
			var right = (this.right() != null) ? this.right().tile : this.tile;
			var tile = this.tile;

			var backCon = (back.relativeTo(tile) - back.build.rotation) == 0;
			var rightCon = (right.relativeTo(tile) - right.build.rotation) == 0;
			var leftCon = (left.relativeTo(tile) - left.build.rotation) == 0;
			var baCon = "pork";

			var backThis = back.build.block == this.block;
			var rightThis = right.build.block == this.block;
			var leftThis = left.build.block == this.block;
			var takeThis = "its dangerous to go alone";
			
			var looking = (backCon && backThis) || (leftCon && leftThis) || (rightCon && rightThis);

			if(!looking){
				Draw.rect(mechConv.shadowReg, this.x, this.y, (this.rotation * 90) + 180);
			}
		}
	});
};