const multiLib = require("libraries/multi-lib");
const multiTestYounggam = multiLib.MultiCrafter(GenericCrafter, "multi-test-younggam", [{
     //1  you can skip recipe properties
        input: {

        },
        output: {
            power: 5.25
        },
        craftTime: 12
    },
    { //2
        input: {
            items: ["coal/1", "sand/1"],
            liquids: ["water/5"],
            power: 1
        },
        output: {
            liquids: ["slag/5"],
        },
        craftTime: 60
    },
    { //3
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
    },
    { //4
        input: {
            items: ["sand/1"],
        },
        output: {
            items: ["silicon/1"],
        },
        craftTime: 30
    },
    { //5
        input: {
            items: ["sand/1", "lead/2"],
            liquids: ["water/5"],
        },
        output: {
            items: ["unity-contagium/1"],
        },
        craftTime: 12
    },
    { //6
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
    },
    { //7
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
    },
    { //8
        input: {
            items: ["sand/1"],
        },
        output: {
            items: ["silicon/1"],
        },
        craftTime: 30
    },
    { //9
        input: {
            items: ["sand/1", "lead/2"],
            liquids: ["water/5"],
        },
        output: {
            items: ["unity-contagium/1"],
        },
        craftTime: 12
    },
    { //10
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
    }
], {

}, {

});
