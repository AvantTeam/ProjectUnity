const monument = extendContent(UnitType, "monument", {});
monument.constructor = () => {
    return extend(LegsUnit, {});
};
