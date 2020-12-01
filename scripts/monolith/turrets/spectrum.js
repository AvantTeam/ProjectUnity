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
        return 36;
    },

    getWidth(){
        return 7;
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
            this.size = (1 + Mathf.sin(Time.time, 15, 0.08 * this.pow)) * this.pow;
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
            Lines.line(Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y);

            for(let i = 0; i < Mathf.signs.length; i++){
                let sign = Mathf.signs[i];
                for(let j = 0; j < 2; j++){
                    let offset = 60 - 30 * j * sign;

                    Drawf.tri(Tmp.v2.x, Tmp.v2.y,
                        1.2 * this.pow + Mathf.absin(2, 1.2) * this.pow,
                        8.5 * this.pow + Mathf.absin(2, 7) * this.pow,
                        this.rotation - 90 + offset
                    );
                };
            };

            //prism
            let w = spectrum.getWidth();
            let shade = 3;

            vecs.get(0).set(Tmp.v31.set(w, -w, w).rotate(axisY, this.rot)).add(this);
            vecs.get(1).set(Tmp.v31.set(w, -w, -w).rotate(axisY, this.rot)).add(this);
            vecs.get(2).set(Tmp.v31.set(-w, -w, w).rotate(axisY, this.rot)).add(this);
            vecs.get(3).set(Tmp.v31.set(-w, -w, -w).rotate(axisY, this.rot)).add(this);
            vecs.get(4).set(Tmp.v31.set(0, w, 0).rotate(axisY, this.rot)).add(this);
            
            let index = 0;
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

            Draw.color(Tmp.c1.set(this.color).mul(
                this.colors.get(0), this.colors.get(0), this.colors.get(0), this.alphas.get(0)
            ));
            Fill.tri(vecs.get(1).x, vecs.get(1).y, vecs.get(0).x, vecs.get(0).y, vecs.get(4).x, vecs.get(4).y);
            Draw.color(Tmp.c1.set(this.color).mul(
                this.colors.get(1), this.colors.get(1), this.colors.get(1), this.alphas.get(1)
            ));
            Fill.tri(vecs.get(1).x, vecs.get(1).y, vecs.get(3).x, vecs.get(3).y, vecs.get(4).x, vecs.get(4).y);
            Draw.color(Tmp.c1.set(this.color).mul(
                this.colors.get(2), this.colors.get(2), this.colors.get(2), this.alphas.get(2)
            ));
            Fill.tri(vecs.get(2).x, vecs.get(2).y, vecs.get(0).x, vecs.get(0).y, vecs.get(4).x, vecs.get(4).y);
            Draw.color(Tmp.c1.set(this.color).mul(
                this.colors.get(3), this.colors.get(3), this.colors.get(3), this.alphas.get(3)
            ));
            Fill.tri(vecs.get(2).x, vecs.get(2).y, vecs.get(3).x, vecs.get(3).y, vecs.get(4).x, vecs.get(4).y);
            Draw.color(Tmp.c1.set(this.color).mul(
                this.colors.get(4), this.colors.get(4), this.colors.get(4), this.alphas.get(4)
            ));
            Fill.quad(vecs.get(0).x, vecs.get(0).y, vecs.get(1).x, vecs.get(1).y, vecs.get(2).x, vecs.get(2).y, vecs.get(3).x, vecs.get(3).y);

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
