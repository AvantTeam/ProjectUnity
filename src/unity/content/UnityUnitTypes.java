package unity.content;

import arc.func.*;
import arc.math.Mathf;
import arc.util.*;
import arc.util.Log.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.content.Bullets;
import unity.libraries.*;

public class UnityUnitTypes implements ContentList{
	private Prov<? extends Unit>[] constructors = new Prov[]{CopterUnit::new};
	public static UnitType
	//flying units
	anthophila, caelifera, lepidoptera, schistocerca, vespula;

	@Override
	public void load(){
		for (int i = 0, j = 0, len = EntityMapping.idMap.length; i < len; i++){
			if (EntityMapping.idMap[i] == null){
				EntityMapping.idMap[i] = constructors[j++];
				if (j >= constructors.length) break;
			}
		}
		EntityMapping.nameMap.put("anthophila", CopterUnit::new);
		anthophila = new CopterUnitType("anthophila", 3){
			{
				speed = 4f;
				drag = 0.07f;
				accel = 0.03f;
				fallSpeed = 0.005f;
				health = 450;
				engineSize = 0f;
				flying = true;
				hitsize = 15f;
				range = 165f;
				weapons.add(new Weapon("anthophila-gun"){
					{
						x = 4.25f;
						y = 14f;
						reload = 15;
						shootSound = Sounds.shootBig;
						bullet = Bullets.standardThoriumBig;
					}
				});
				weapons.add(new Weapon("anthophila-tesla"){
					{
						x = 7.75f;
						y = 8.25f;
						reload = 30f;
						shots = 5;
						shootSound = Sounds.spark;
						bullet = new LightningBulletType(){
							{
								damage = 15f;
							}
						};
					}
				});
				int index = 0;
				fallRotateSpeed = 2f;
				rotors[index++] = new Rotor(0f, -13f, 0.6f, 4, 29, 0);
				for (int i = 0; i < 2; i++)
					rotors[index++] = new Rotor(13f * Mathf.signs[i], 3f, 1f, 3, 29 * Mathf.signs[i], i * 180);
			}
		};
		EntityMapping.nameMap.put("caelifera", CopterUnit::new);
		caelifera = new CopterUnitType("caelifera", 1){
			{
				rotors[0] = new Rotor(0f, 6f, 1f, 4, 29, 0);
			}
		};
		EntityMapping.nameMap.put("lepidoptera", CopterUnit::new);
		lepidoptera = new CopterUnitType("lepidoptera", 8){
			{
				fallRotateSpeed = 0.8f;
				for (int i = 0, index = 0; i < 2; i++){
					for (int j = 0; j < 2; j++){
						rotors[index++] = new Rotor(Mathf.signs[i] * 22.5f, 21.25f, 1f, 3,
							19 * Mathf.signs[i] * Mathf.signs[j], 0);
						rotors[index++] = new Rotor(Mathf.signs[i] * 17.25f, 1f, 0.8f, 2,
							23 * Mathf.signs[i] * Mathf.signs[j], 0);
					}
				}
			}
		};
		EntityMapping.nameMap.put("schistocerca", CopterUnit::new);
		schistocerca = new CopterUnitType("schistocerca", 2){
			{
				for (int i = 0, index = 0; i < 2; i++, index++)
					rotors[index] = new Rotor(0f, 6.5f, 1f, 3, 29 * Mathf.signs[i], 0);
			}
		};
		EntityMapping.nameMap.put("vespula", CopterUnit::new);
		vespula = new CopterUnitType("vespula", 4){
			{
				for (int i = 0, index = 0; i < 2; i++){
					for (int j = 0; j < 2; j++) rotors[index++] = new Rotor(15f * Mathf.signs[i], 6.75f, 1f, 4,
						29 * Mathf.signs[i] * Mathf.signs[j], j * 180);
				}
			}
		};
	}
}
