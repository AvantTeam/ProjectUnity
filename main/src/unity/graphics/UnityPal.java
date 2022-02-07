package unity.graphics;

import arc.graphics.*;
import mindustry.graphics.*;

import static arc.graphics.Color.*;

public class UnityPal{
    public static Color

    plague = valueOf("a3f080"),
    plagueDark = valueOf("54de3b"),

    scarColor = valueOf("f53036"),
    scarColorAlpha = valueOf("f5303690"),

    monolithLight = valueOf("c0ecff"),
    monolith = valueOf("87ceeb"),
    monolithDark = valueOf("6586b0"),
    monolithAtmosphere = valueOf("001e6360"),

    advance = valueOf("a3e3ff"),
    advanceDark = valueOf("59a7ff"),
    wavefrontDark = valueOf("9e9f9f"),

    lightLight = valueOf("a0ffff"),
    lightMid = valueOf("50ecff"),
    lightDark = valueOf("00d9ff"),

    lightHeat = valueOf("ccffff"),
    lightEffect = valueOf("4787ff"),

    purpleLightning = valueOf("bf92f9"),

    endColor = valueOf("ff786e"),

    imberColor = valueOf("fff566"),

    navalReddish = valueOf("d4816b"),
    navalYellowish = valueOf("ffd37f"),

    laserOrange = valueOf("ff9c5a"),

    expLaser = valueOf("F9DBB1"),
    exp = valueOf("84ff00"),
    expMax = valueOf("90ff00"),
    expBack = valueOf("4d8f07"),
    lava = valueOf("ff2a00"),
    lava2 = valueOf("ffcc00"),
    dense = valueOf("ffbeb8"),
    dirium = valueOf("96f7c3"),
    diriumLight = valueOf("ccffe4"),
    coldColor = valueOf("6bc7ff"),
    bgCol = valueOf("323232"),
    deepRed = Color.valueOf("f25555"),
    deepBlue = Color.valueOf("554deb"),

    lancerSap1 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.167f),
    lancerSap2 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.333f),
    lancerSap3 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f),
    lancerSap4 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.667f),
    lancerSap5 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.833f),

    lancerDir1 = Pal.lancerLaser.cpy().lerp(diriumLight, 0.25f),
    lancerDir2 = Pal.lancerLaser.cpy().lerp(diriumLight, 0.5f),
    lancerDir3 = Pal.lancerLaser.cpy().lerp(diriumLight, 0.75f),

    youngchaGray = valueOf("555555"),

    blueprintCol = valueOf("354654"),

    outline = Pal.darkerMetal,
    darkOutline = valueOf("38383d"),
    darkerOutline = valueOf("2e3142");
}
