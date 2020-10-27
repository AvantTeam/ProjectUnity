const axisX = new Vec3(0, 1, 0);
const axisY = new Vec3(0, 0, -1);

const vecs = Seq.with(
    new Vec3(),
    new Vec3(),
    new Vec3(),
    new Vec3(),
    new Vec3()
);
const sides = Seq.with(
    IntSeq.with(1, 0, 4),
    IntSeq.with(1, 3, 4),
    IntSeq.with(2, 0, 4),
    IntSeq.with(2, 3, 4),
    IntSeq.with(0, 1, 2, 3)
);

const trailEffect = new Effect(30, e => {
    if(
        e.data == null ||
        typeof(e.data.dist) === "undefined" ||
        typeof(e.color) === "undefined" ||
        typeof(e.data.size) === "undefined"
    ) return;

    let dist = e.finpow() * 10 * e.data.dist;
    let x = e.x + Angles.trnsx(e.rotation, 0, dist);
    let y = e.y + Angles.trnsy(e.rotation, 0, dist);

    Draw.color(e.color);
    Fill.circle(x, y, e.fout() * 1.8 * e.data.size);
});

const spectrum = extendContent(PowerTurret, "spectrum", {
    getPrismOffset(){
        return 32;
    },

    getWidth(){
        return 9;
    },

    getTimer(){
        return this.trailTimer;
    }
});
spectrum.trailTimer = spectrum.timers++;

spectrum.buildType = () => {
    const build = extendContent(PowerTurret.PowerTurretBuild, spectrum, {
        updateTile(){
            this.super$updateTile();

            this.pow = Mathf.lerpDelta(this.pow, this.efficiency(), spectrum.cooldown);
            this.rot += 2 * this.pow;
            this.size = (1 + Mathf.sin(Time.time(), 15, 0.08 * this.pow)) * this.pow;
            this.color.shiftHue(this.rot).mul(0.5 + this.pow * 0.5);
        },

        draw(){
            this.super$draw();

            //lines
            Tmp.v1.trns(this.rotation, -this.recoil / 2).add(this);
            Tmp.v2.trns(this.rotation, -this.recoil / 2 + spectrum.size * 4).add(this);
            Tmp.v3.trns(this.rotation, spectrum.getPrismOffset() - this.recoil).add(this);

            let z = Draw.z();
            Draw.z(Layer.power);

            Draw.color(this.color);
            Fill.circle(Tmp.v2.x, Tmp.v2.y, 1.2 * this.pow + Mathf.absin(5, 1) * this.pow);
            Fill.circle(Tmp.v3.x, Tmp.v3.y, 1.2 * this.pow + Mathf.absin(5, 1) * this.pow);

            Lines.stroke(0.4 * this.pow + Mathf.absin(5, 0.2) * this.pow);
            Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);

            for(let i = 0; i < Mathf.signs.length; i++){
                let sign = Mathf.signs[i];
                for(let j = 0; j < 2; j++){
                    let offset = 60 - 30 * j * sign;

                    Drawf.tri(Tmp.v1.x, Tmp.v1.y,
                        1.2 * this.pow + Mathf.absin(2, 1.2) * this.pow,
                        8.5 * this.pow + Mathf.absin(2, 7) * this.pow,
                        this.rotation - 90 + offset
                    );
                };
            };

            //prism
            let w = spectrum.getWidth();
            let shade = 3;

            vecs.get(0).set(Tmp.v31.set(w, -w, w).rotate(axisY, this.rot));
            vecs.get(1).set(Tmp.v31.set(w, -w, -w).rotate(axisY, this.rot));
            vecs.get(2).set(Tmp.v31.set(-w, -w, w).rotate(axisY, this.rot));
            vecs.get(3).set(Tmp.v31.set(-w, -w, -w).rotate(axisY, this.rot));
            vecs.get(4).set(Tmp.v31.set(0, w, 0).rotate(axisY, this.rot));
            
            let index = Math.round(0);
            vecs.each(vec => {
                vec.rotate(axisX, this.rotation - 90);

                let a;
                let c;
                for(let h = 0; h < sides.get(index).items.length; h++){
                    let j = sides.get(index).items[h];
                    a += vecs.get(j).z;
                    c += vecs.get(j).z;
                };
                a = a <= 0 ? 0 : this.pow;
                c = Mathf.clamp(c / w / shade);

                this.alphas.set(index, a);
                this.colors.set(index, c);
                index++;
            });

            if(this.timer.get(spectrum.getTimer(), 1 / this.pow)){
                let rand = Mathf.range(w / 1.6) * spectrum.size;

                trailEffect.at(
                    this.x + Angles.trnsx(this.rotation - 90, rand, spectrum.getPrismOffset() - (w * this.size) - this.recoil),
                    this.y + Angles.trnsy(this.rotation - 90, rand, spectrum.getPrismOffset() - (w * this.size) - this.recoil),
                    this.rotation + 90, this.color, {
                        dist: Mathf.random(0.8, 1.2),
                        size: this.pow
                    }
                );
            };

            sides.each(side => {
                let region = Core.atlas.white();
                let color = Color.whiteFloatBits;
                let mcolor = Color.clearFloatBits;
                let u = region.u;
                let v = region.v;

                let vertices = [];
                for(let i = 0; i < 24; i++) vertices[i] = null;

                for(let i = 0; i < side.items.length; i++){
                    let point = vecs.get(side.items[i]);

                    vertices[Math.round(0)] = point.x;
                    vertices[Math.round(1)] = point.y;
                    vertices[Math.round(2)] = color;
                    vertices[Math.round(3)] = u;
                    vertices[Math.round(4)] = v;
                    vertices[Math.round(5)] = mcolor;
                };

                Draw.vert(region.texture, vertices, 0, vertices.length);
            });

            Draw.z(z);
            Draw.reset();
        }
    });
    build.pow = 0;
    build.rot = 0;
    build.size = 0;
    build.color = Color.valueOf("ffcccc");
    build.alphas = Seq.with(0, 0, 0, 0, 0);
    build.colors = Seq.with(0, 0, 0, 0, 0);

    return build;
}
