const lavaColor = Color.valueOf("ff2a00");
const lavaColor2 = Color.valueOf("ffcc00");

const ahhimaliquidnow = new Effect(45, e => {
    Draw.color(Color.gray, Color.clear, e.fin());
    Angles.randLenVectors(e.id, 3, 2.5 + e.fin() * 6, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 0.2 + e.fin() * 3);
    });

    Draw.color(lavaColor, lavaColor2, e.fout());

    Angles.randLenVectors(e.id, 4, 1 + e.fin() * 4, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 0.2 + e.fout() * 1.3);
    });
});

const helpitburns = new StatusEffect("molten");
helpitburns.color = lavaColor;
helpitburns.speedMultiplier = 0.6;
helpitburns.healthMultiplier = 0.5;
helpitburns.damage = 1;
helpitburns.effect = ahhimaliquidnow;

const lava = new Liquid("lava", lavaColor);
lava.heatCapacity = 0;
lava.viscosity = 0.7;
lava.temperature = 1.5;
lava.effect = helpitburns;
lava.lightColor = lavaColor2.cpy().mul(1, 1, 1, 0.55);

var tmpc = new Color(0, 0, 0, 1);//Tmp.c1 cannot be used in events.run
var tmpc2 = new Color(0, 0, 0, 1);

Events.run(Trigger.update, () => {
    lava.color = tmpc.set(lavaColor).lerp(lavaColor2, Mathf.absin(Time.globalTime(), 25, 1));
    lava.lightColor = tmpc2.set(lava.color).mul(1, 1, 1, 0.55);
});
