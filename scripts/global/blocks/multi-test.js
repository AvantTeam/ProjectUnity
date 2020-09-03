const multiTest = this.global.unity.multiCrafter.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "multi-test", {
	rawConsumes: [
		{
			items: [
				{item: "copper", amount: 1}
			],
			liquid: {
				liquid: "cryofluid",
				amount: 1
			},
			power: 1
		},
		
		{
			items: [
				{item: "copper", amount: 1},
				{item: "unity-imberium", amount: 3}
			],
			liquid: {
				liquid: "water",
				amount: 2
			},
			power: 1.5
		}
	],
	
	outputItems: [
		{item: "lead", amount: 3},
		{item: "titanium", amount: 2}
	],
	
	outputLiquids: [
		{liquid: "slag", amount: 60}
	],
	
	craftTimes: [60, 40]
}, {});
multiTest.category = Category.crafting;
print(Object.keys(multiTest));
