package com.tms.threed.featureModel.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface CommonVarNames  {
    
    String ModelCode = "ModelCode";
    String Trim = "Trim";
    String Base = "Base";
    String Color = "Color";
    String ExteriorColor = "ExteriorColor";
    String InteriorColor = "InteriorColor";
    String InteriorColorCode = "InteriorColorCode";
    String FabricPlusColor = "FabricPlusColor";
    String InteriorMaterial = "InteriorMaterial";
    String InteriorMaterialCode = "InteriorMaterialCode";
    String Grade = "Grade";
    String Engine = "Engine";
    String Transmission = "Transmission";

    String Options = "Options";

    String Accessories = "Accessories";
    String InteriorAccessories = "InteriorAccessories";
    String ExteriorAccessories = "ExteriorAccessories";


/*

J-X:
    ExteriorAccessories
    FabricPlusColor
    InteriorColor
    InteriorMaterial
    InteriorAccessories
    ExteriorColor
    Color
    Trim
    Engine
    ModelCode
    Grade
    Transmission
    Options
    Accessories





X-J:
    interior
    exterior
    engine
    transmission
    iMaterial
    modelCode
    grade
    trim
    options
    accessories
    color
    iColor

*/

}
