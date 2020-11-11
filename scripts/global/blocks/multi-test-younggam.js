const multiLib = require("libraries/multi-lib");
const multiTest1 = multiLib.MultiCrafter(GenericSmelter, GenericSmelter.SmelterBuild, "multi-test-1", [{
        //1  you can skip recipe properties
        input: {},
        output: {
            power: 5.25
        },
        craftTime: 12
    }, { //2
        input: {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output: {
            liquids: ["slag/5"],
        },
        craftTime: 60
    }, { //3
        input: {
            items: ["pyratite/1", "blast-compound/1"],
            liquids: ["water/5"],
            power: 1
        },
        output: {
            items: ["scrap/1", "plastanium/2", "spore-pod/2"],
            liquids: ["oil/5"],
        },
        craftTime: 72
    }, { //4
        input: {
            items: ["sand/1"],
        },
        output: {
            items: ["silicon/1"],
        },
        craftTime: 30
    }, { //5
        input: {
            items: ["sand/1", "lead/2"],
            liquids: ["water/5"],
        },
        output: {
            items: ["unity-contagium/1"],
        },
        craftTime: 12
    }, { //6
        input: {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output: {
            items: ["thorium/1", "surge-alloy/2"],
            liquids: ["slag/5"],
        },
        craftTime: 60
    }, { //7
        input: {
            items: ["pyratite/1", "blast-compound/1"],
            liquids: ["water/5"],
            power: 1
        },
        output: {
            items: ["scrap/1", "plastanium/2", "spore-pod/2"],
            liquids: ["oil/5"],
        },
        craftTime: 72
    }, { //8
        input: {
            items: ["sand/1"],
        },
        output: {
            items: ["silicon/1"],
        },
        craftTime: 30
    }, { //9
        input: {
            items: ["sand/1", "lead/2"],
            liquids: ["water/5"],
        },
        output: {
            items: ["unity-contagium/1"],
        },
        craftTime: 12
    }, { //10
        input: {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output: {
            items: ["thorium/1", "surge-alloy/2"],
            liquids: ["slag/5", "oil/5"],
        },
        craftTime: 60
    }], {}, //for entity, they need constructor for extra properties
    function Extra() {
        this._obj = {
            a: Time.time()
        };
        this.getObj = function() {
            return this._obj;
        };
        this.add = function() {
            this.super$add();
            print(this._obj.a);
        }
    });
multiTest1.dumpToggle = true;
const multiTest2 = multiLib.MultiCrafter(GenericCrafter, GenericCrafter.GenericCrafterBuild, "multi-test-2", [{ //1  you can skip recipe properties
    input: {
        items: ["sand/1", "lead/1"],
    },
    craftTime: 12
}, { //2
    input: {
        items: ["coal/1", "sand/1"],
    },
    output: {
        items: ["thorium/1", "surge-alloy/2"],
        power: 10
    },
    craftTime: 60
}, { //3
    input: {
        items: ["pyratite/1", "blast-compound/1"],
    },
    output: {
        items: ["scrap/1", "plastanium/2", "spore-pod/2"],
    },
    craftTime: 72
}, { //4
    input: {
        items: ["sand/1"],
        power: 15
    },
    output: {
        items: ["silicon/1"],
        power: 10
    },
    craftTime: 30
}], {}, {});
