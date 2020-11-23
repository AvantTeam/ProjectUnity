const lavaColor = Color.valueOf("ff2a00");
const lavaColor2 = Color.valueOf("ffcc00");

const lava = new Liquid("lava", lavaColor);
lava.heatCapacity = 0;
lava.viscosity = 1;
lava.temperature = 1.5;
lava.effect = StatusEffects.melting;
lava.lightColor = lavaColor2;

var tmpc = new Color(0, 0, 0, 1);//Tmp.c1 cannot be used in events.run

Events.run(Trigger.update, () => {
    lava.color = tmpc.set(lavaColor).lerp(lavaColor2, Mathf.absin(Time.globalTime(), 25, 1));
});
