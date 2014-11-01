 /* Copyright 2012 Yaqiang Wang,
 * yaqiang.wang@gmail.com
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 */
package org.meteoinfo.legend;

import org.meteoinfo.data.GridData;
import org.meteoinfo.data.StationData;
import org.meteoinfo.drawing.ContourDraw;
import org.meteoinfo.drawing.PointStyle;
import org.meteoinfo.global.MIMath;
import org.meteoinfo.shape.ShapeTypes;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.meteoinfo.global.util.BigDecimalUtil;
import org.meteoinfo.global.DataConvert;
import org.meteoinfo.layer.VectorLayer;
import org.meteoinfo.shape.PointShape;
import org.meteoinfo.shape.PolygonShape;
import org.meteoinfo.shape.PolylineShape;
import org.meteoinfo.shape.Shape;

/**
 * Legend manage class
 *
 * @author yaqiang
 */
public class LegendManage {

    /**
     * Create legend scheme from grid data
     *
     * @param aGridData Grid data
     * @param aLT Legend type
     * @param aST Shape type
     * @return Legend scheme
     */
    public static LegendScheme createLegendSchemeFromGridData(GridData aGridData,
            LegendType aLT, ShapeTypes aST) {
        LegendScheme aLS;
        double[] CValues;
        Color[] colors;
        double[] maxmin = new double[2];
        boolean hasUndef = aGridData.getMaxMinValue(maxmin);
        double MinData = maxmin[1];
        double MaxData = maxmin[0];

        CValues = createContourValues(MinData, MaxData);
        colors = createRainBowColors(CValues.length + 1);

//        List<String> values = new ArrayList<String>();
//        //String dFormat = "%1$." + String.valueOf(MIMath.getDecimalNum(CValues[0])) + "f";
//        for (double v : CValues) {
//            //values.add(String.format(dFormat, v));
//            values.add(String.valueOf(v));
//        }
        //Generate lengendscheme  
        if (aLT == LegendType.UniqueValue) {
            aLS = createUniqValueLegendScheme(CValues, colors,
                    aST, MinData, MaxData, hasUndef, aGridData.missingValue);
        } else {
            aLS = createGraduatedLegendScheme(CValues, colors,
                    aST, MinData, MaxData, hasUndef, aGridData.missingValue);
        }

        return aLS;
    }

    /**
     * Create legend scheme from station data
     *
     * @param stationData Station data
     * @param aLT Legend type
     * @param aST Shape type
     * @return Legend scheme
     */
    public static LegendScheme createLegendSchemeFromStationData(StationData stationData,
            LegendType aLT, ShapeTypes aST) {
        LegendScheme aLS;
        double[] CValues;
        Color[] colors;
        double MinData;
        double MaxData;
        double[] minmax = new double[2];
        boolean hasNoData = ContourDraw.getMinMaxValueFDiscreteData(stationData.data, stationData.missingValue, minmax);
        MinData = minmax[0];
        MaxData = minmax[1];
        CValues = createContourValues(MinData, MaxData);
        colors = createRainBowColors(CValues.length + 1);

        //Generate lengendscheme                       
        if (aLT == LegendType.UniqueValue) {
            aLS = createUniqValueLegendScheme(CValues, colors,
                    aST, MinData, MaxData, hasNoData, stationData.missingValue);
        } else {
            aLS = createGraduatedLegendScheme(CValues, colors,
                    aST, MinData, MaxData, hasNoData, stationData.missingValue);
        }

        return aLS;
    }

    /**
     * Create single symbol legend scheme
     *
     * @param shapeType The shape type
     * @return Legend scheme
     */
    public static LegendScheme createSingleSymbolLegendScheme(ShapeTypes shapeType) {
        if (shapeType.isPoint()) {
            return createSingleSymbolLegendScheme(shapeType, Color.green, 8.0f);
        } else if (shapeType.isLine()) {
            return createSingleSymbolLegendScheme(shapeType, Color.blue, 1.0f);
        } else {
            return createSingleSymbolLegendScheme(shapeType, Color.lightGray, 1.0f);
        }
    }

    /**
     * Create single symbol legend scheme
     *
     * @param aST Shape type
     * @param aColor Color
     * @param size Size
     * @return Legend scheme
     */
    public static LegendScheme createSingleSymbolLegendScheme(ShapeTypes aST, Color aColor,
            float size) {
        LegendScheme legendScheme = new LegendScheme(aST);
        legendScheme.setLegendType(LegendType.SingleSymbol);
        legendScheme.setShapeType(aST);
        legendScheme.setMinValue(0);
        legendScheme.setMaxValue(0);
        legendScheme.setUndefValue(-9999);
        legendScheme.setLegendBreaks(new ArrayList<ColorBreak>());
        if (aST.isPoint()) {
            PointBreak aPB = new PointBreak();
            aPB.setColor(aColor);
            aPB.setOutlineColor(Color.black);
            aPB.setSize(size);
            aPB.setNoData(false);
            aPB.setDrawFill(true);
            aPB.setDrawOutline(true);
            aPB.setDrawShape(true);
            aPB.setStyle(PointStyle.Circle);
            aPB.setStartValue(0);
            aPB.setEndValue(0);
            aPB.setCaption("");
            legendScheme.getLegendBreaks().add(aPB);
        } else if (aST.isLine()) {
            PolylineBreak aPLB = new PolylineBreak();
            aPLB.setColor(aColor);
            aPLB.setDrawPolyline(true);
            aPLB.setSize(size);
            aPLB.setStyle(LineStyles.Solid);
            aPLB.setStartValue(0);
            aPLB.setEndValue(0);
            aPLB.setCaption("");
            aPLB.setSymbolColor(aColor);
            legendScheme.getLegendBreaks().add(aPLB);
        } else if (aST.isPolygon()) {
            PolygonBreak aPGB = new PolygonBreak();
            aPGB.setColor(aColor);
            aPGB.setDrawFill(true);
            aPGB.setDrawOutline(true);
            aPGB.setDrawShape(true);
            aPGB.setOutlineColor(Color.gray);
            aPGB.setOutlineSize(size);
            aPGB.setStartValue(0);
            aPGB.setEndValue(0);
            aPGB.setCaption("");
            legendScheme.getLegendBreaks().add(aPGB);

        }

        return legendScheme;
    }

    /**
     * Create unique value legend scheme
     *
     * @param CValues The values
     * @param captions The captions
     * @param colors The colors
     * @param aST The shape type
     * @param min Minimum value
     * @param max Maximum value
     * @param hasNodata If has undefine data
     * @param unDef Undefine data
     * @return The legend scheme
     */
    public static LegendScheme createUniqValueLegendScheme(List<String> CValues, List<String> captions, Color[] colors, ShapeTypes aST,
            double min, double max, boolean hasNodata, double unDef) {
        LegendScheme legendScheme = new LegendScheme(aST);
        legendScheme.setLegendType(LegendType.UniqueValue);
        legendScheme.setShapeType(aST);
        legendScheme.setMinValue(min);
        legendScheme.setMaxValue(max);
        legendScheme.setUndefValue(unDef);
        int i;
        List<Integer> idxList = new ArrayList<Integer>();
        switch (aST) {
            case Point:
            case PointZ:
                for (i = 1; i < colors.length; i++) {
                    PointBreak aPB = new PointBreak();
                    aPB.setColor(colors[i]);
                    aPB.setStartValue(CValues.get(i - 1));
                    aPB.setEndValue(aPB.getStartValue());
                    if (colors.length <= 13) {
                        aPB.setSize((float) i / 2 + 2);
                    } else {
                        aPB.setSize(5);
                    }
                    aPB.setStyle(PointStyle.Circle);
                    aPB.setOutlineColor(Color.black);
                    aPB.setNoData(false);
                    aPB.setDrawOutline(true);
                    aPB.setDrawFill(true);
                    aPB.setDrawShape(true);
                    aPB.setCaption(captions.get(i - 1));

                    legendScheme.getLegendBreaks().add(aPB);
                }
                legendScheme.setHasNoData(false);
                break;
            case Polyline:
            case PolylineM:
            case PolylineZ:
                for (i = 1; i < colors.length; i++) {
                    PolylineBreak aPLB = new PolylineBreak();
                    aPLB.setColor(colors[i]);
                    aPLB.setStartValue(CValues.get(i - 1));
                    aPLB.setEndValue(aPLB.getStartValue());
                    aPLB.setSize(1.0F);
                    aPLB.setStyle(LineStyles.Solid);
                    aPLB.setDrawPolyline(true);
                    aPLB.setCaption(captions.get(i - 1));
                    aPLB.setSymbolColor(aPLB.getColor());
                    aPLB.setSymbolStyle(PointStyle.Circle);

                    legendScheme.getLegendBreaks().add(aPLB);
                }
                legendScheme.setHasNoData(false);
                break;
            case Polygon:
                for (i = 1; i < colors.length; i++) {
                    PolygonBreak aPGB = new PolygonBreak();
                    aPGB.setColor(colors[i]);
                    aPGB.setOutlineColor(Color.gray);
                    aPGB.setOutlineSize(1.0F);
                    aPGB.setDrawFill(true);
                    aPGB.setDrawOutline(true);
                    aPGB.setDrawShape(true);
                    aPGB.setStartValue(CValues.get(i - 1));
                    aPGB.setEndValue(aPGB.getStartValue());
                    aPGB.setCaption(captions.get(i - 1));
                    //aPGB.Style = (HatchStyle)idxList[i];

                    legendScheme.getLegendBreaks().add(aPGB);
                }
                legendScheme.setHasNoData(false);
                break;
        }

        return legendScheme;
    }

    /**
     * Create unique value legend scheme
     *
     * @param CValues The values
     * @param colors The colors
     * @param aST The shape type
     * @param min Minimum value
     * @param max Maximum value
     * @param hasNodata If has undefine data
     * @param unDef Undefine data
     * @return The legend scheme
     */
    public static LegendScheme createUniqValueLegendScheme(List<String> CValues, Color[] colors, ShapeTypes aST,
            double min, double max, boolean hasNodata, double unDef) {
        return createUniqValueLegendScheme(CValues, CValues, colors, aST, min, max, hasNodata, unDef);
    }

    /**
     * Create unique value legend scheme
     *
     * @param CValues The values
     * @param colors The colors
     * @param aST The shape type
     * @param min Minimum value
     * @param max Maximum value
     * @param hasNodata If has undefine data
     * @param unDef Undefine data
     * @return The legend scheme
     */
    public static LegendScheme createUniqValueLegendScheme(double[] CValues, Color[] colors, ShapeTypes aST,
            double min, double max, Boolean hasNodata, double unDef) {
        List<String> values = new ArrayList<String>();
        List<String> captions = new ArrayList<String>();
        String dFormat = "%1$." + String.valueOf(MIMath.getDecimalNum(CValues[0])) + "f";
        for (double v : CValues) {
            captions.add(String.format(dFormat, v));
            values.add(String.valueOf(v));
        }

        return createUniqValueLegendScheme(values, captions, colors, aST, min, max, hasNodata, unDef);
    }

    /**
     * Create unique value legend scheme from a vector layer
     *
     * @param aLayer Vector layer
     * @param min Minimum
     * @param max Maximum
     * @return Legend scheme
     */
    public static LegendScheme createUniqValueLegendScheme(VectorLayer aLayer, double min, double max) {
        LegendScheme aLS = new LegendScheme(aLayer.getShapeType());
        double[] CValues;
        Color[] colors;
        List<Double> valueList = new ArrayList<Double>();

        switch (aLayer.getShapeType()) {
            case Point:
                for (Shape aPS : aLayer.getShapes()) {
                    if (!valueList.contains(((PointShape) aPS).getValue())) {
                        valueList.add(((PointShape) aPS).getValue());
                    }
                }
                break;
            case Polyline:
            case PolylineZ:
                for (Shape aPLS : aLayer.getShapes()) {
                    if (!valueList.contains(((PolylineShape) aPLS).value)) {
                        valueList.add(((PolylineShape) aPLS).value);
                    }
                }
                break;
            default:
                for (Shape aPGS : aLayer.getShapes()) {
                    if (!valueList.contains(((PolygonShape) aPGS).lowValue)) {
                        valueList.add(((PolygonShape) aPGS).lowValue);
                    }
                }
                break;
        }

        CValues = new double[valueList.size()];
        for (int i = 0; i < valueList.size(); i++) {
            CValues[i] = valueList.get(i);
        }

        if (CValues.length <= 13) {
            colors = createRainBowColors(CValues.length);
        } else {
            colors = createRandomColors(CValues.length);
        }
        Color[] newcolors = new Color[colors.length + 1];
        newcolors[0] = Color.white;
        for (int i = 1; i < newcolors.length; i++) {
            newcolors[i] = colors[i - 1];
        }

        aLS = createUniqValueLegendScheme(CValues, newcolors,
                aLayer.getShapeType(), min, max, false, -9999);

        return aLS;
    }

    /**
     * Create graduated color legend scheme
     *
     * @param CValues The values
     * @param colors The colors
     * @param aST Shape type
     * @param min Minimum value
     * @param max Maximum value
     * @param hasNodata Is has no data
     * @param unDef Undefine data
     * @return The legend scheme
     */
    public static LegendScheme createGraduatedLegendScheme(double[] CValues, Color[] colors, ShapeTypes aST,
            double min, double max, Boolean hasNodata, double unDef) {
        LegendScheme legendScheme = new LegendScheme(aST);
        legendScheme.setLegendType(LegendType.GraduatedColor);
        legendScheme.setShapeType(aST);
        legendScheme.setMinValue(min);
        legendScheme.setMaxValue(max);
        legendScheme.setUndefValue(unDef);
        int i;
        switch (aST) {
            case Point:
                for (i = 0; i < colors.length; i++) {
                    PointBreak aPB = new PointBreak();
                    aPB.setColor(colors[i]);
                    aPB.setOutlineColor(Color.black);
                    aPB.setNoData(false);
                    aPB.setDrawOutline(true);
                    aPB.setDrawFill(true);
                    aPB.setDrawShape(true);
                    if (i == 0) {
                        aPB.setStartValue(min);
                    } else {
                        aPB.setStartValue(CValues[i - 1]);
                    }
                    if (i == colors.length - 1) {
                        aPB.setEndValue(max);
                    } else {
                        aPB.setEndValue(CValues[i]);
                    }
                    //aPB.setSize((float) i / 2 + 2);
                    aPB.setSize(8);
                    aPB.setStyle(PointStyle.Circle);
                    if (aPB.getStartValue() == aPB.getEndValue()) {
                        aPB.setCaption(DataConvert.removeTailingZeros(aPB.getStartValue().toString()));
                    } else {
                        if (i == 0) {
                            aPB.setCaption("< " + DataConvert.removeTailingZeros(aPB.getEndValue().toString()));
                        } else if (i == colors.length - 1) {
                            aPB.setCaption("> " + DataConvert.removeTailingZeros(aPB.getStartValue().toString()));
                        } else {
                            aPB.setCaption(DataConvert.removeTailingZeros(aPB.getStartValue().toString())
                                    + " - " + DataConvert.removeTailingZeros(aPB.getEndValue().toString()));
                        }
                    }

                    legendScheme.getLegendBreaks().add(aPB);
                }
                legendScheme.setHasNoData(false);
                if (hasNodata) {
                    PointBreak aPB = new PointBreak();
                    aPB.setColor(Color.gray);
                    aPB.setOutlineColor(Color.black);
                    aPB.setStartValue(unDef);
                    aPB.setEndValue(aPB.getStartValue());
                    aPB.setSize(1);
                    aPB.setStyle(PointStyle.Circle);
                    aPB.setCaption("NoData");
                    aPB.setNoData(true);
                    aPB.setDrawShape(true);
                    aPB.setDrawOutline(true);
                    legendScheme.getLegendBreaks().add(aPB);
                    legendScheme.setHasNoData(true);
                }
                break;
            case Polyline:
            case PolylineZ:
                for (i = 0; i < colors.length; i++) {
                    PolylineBreak aPLB = new PolylineBreak();
                    aPLB.setColor(colors[i]);
                    aPLB.setSize(1.0F);
                    aPLB.setStyle(LineStyles.Solid);
                    aPLB.setDrawPolyline(true);
                    if (i == 0) {
                        aPLB.setStartValue(min);
                    } else {
                        aPLB.setStartValue(CValues[i - 1]);
                    }
                    if (i == colors.length - 1) {
                        aPLB.setEndValue(max);
                    } else {
                        aPLB.setEndValue(CValues[i]);
                    }
                    if (aPLB.getStartValue() == aPLB.getEndValue()) {
                        aPLB.setCaption(DataConvert.removeTailingZeros(aPLB.getStartValue().toString()));
                    } else {
                        if (i == 0) {
                            aPLB.setCaption("< " + DataConvert.removeTailingZeros(aPLB.getEndValue().toString()));
                        } else if (i == colors.length - 1) {
                            aPLB.setCaption("> " + DataConvert.removeTailingZeros(aPLB.getStartValue().toString()));
                        } else {
                            aPLB.setCaption(DataConvert.removeTailingZeros(aPLB.getStartValue().toString())
                                    + " - " + DataConvert.removeTailingZeros(aPLB.getEndValue().toString()));
                        }
                    }
                    aPLB.setSymbolColor(aPLB.getColor());
                    if (i < PointStyle.values().length) {
                        aPLB.setSymbolStyle(PointStyle.values()[i]);
                    }

                    legendScheme.getLegendBreaks().add(aPLB);
                }
                legendScheme.setHasNoData(false);
                break;
            case Polygon:
                for (i = 0; i < colors.length; i++) {
                    PolygonBreak aPGB = new PolygonBreak();
                    aPGB.setColor(colors[i]);
                    aPGB.setOutlineColor(Color.gray);
                    aPGB.setOutlineSize(1.0F);
                    aPGB.setDrawFill(true);
                    aPGB.setDrawOutline(false);
                    aPGB.setDrawShape(true);
                    if (i == 0) {
                        aPGB.setStartValue(min);
                    } else {
                        aPGB.setStartValue(CValues[i - 1]);
                    }
                    if (i == colors.length - 1) {
                        aPGB.setEndValue(max);
                    } else {
                        aPGB.setEndValue(CValues[i]);
                    }
                    if (aPGB.getStartValue() == aPGB.getEndValue()) {
                        aPGB.setCaption(DataConvert.removeTailingZeros(aPGB.getStartValue().toString()));
                    } else {
                        if (i == 0) {
                            aPGB.setCaption("< " + DataConvert.removeTailingZeros(aPGB.getEndValue().toString()));
                        } else if (i == colors.length - 1) {
                            aPGB.setCaption("> " + DataConvert.removeTailingZeros(aPGB.getStartValue().toString()));
                        } else {
                            aPGB.setCaption(DataConvert.removeTailingZeros(aPGB.getStartValue().toString())
                                    + " - " + DataConvert.removeTailingZeros(aPGB.getEndValue().toString()));
                        }
                    }
//                        if (Enum.IsDefined(typeof(HatchStyle), i))
//                            aPGB.Style = (HatchStyle)i;

                    legendScheme.getLegendBreaks().add(aPGB);
                }
                legendScheme.setHasNoData(false);
                break;
            case Image:
                for (i = 0; i < colors.length; i++) {
                    ColorBreak aCB = new ColorBreak();
                    aCB.setColor(colors[i]);
                    if (i == 0) {
                        aCB.setStartValue(min);
                    } else {
                        aCB.setStartValue(CValues[i - 1]);
                    }
                    if (i == colors.length - 1) {
                        aCB.setEndValue(max);
                    } else {
                        aCB.setEndValue(CValues[i]);
                    }
                    if (aCB.getStartValue() == aCB.getEndValue()) {
                        aCB.setCaption(DataConvert.removeTailingZeros(aCB.getStartValue().toString()));
                    } else {
                        if (i == 0) {
                            aCB.setCaption("< " + DataConvert.removeTailingZeros(aCB.getEndValue().toString()));
                        } else if (i == colors.length - 1) {
                            aCB.setCaption("> " + DataConvert.removeTailingZeros(aCB.getStartValue().toString()));
                        } else {
                            aCB.setCaption(DataConvert.removeTailingZeros(aCB.getStartValue().toString())
                                    + " - " + DataConvert.removeTailingZeros(aCB.getEndValue().toString()));
                        }
                    }

                    legendScheme.getLegendBreaks().add(aCB);
                }
                legendScheme.setHasNoData(false);
                if (hasNodata) {
                    ColorBreak aCB = new ColorBreak();
                    aCB.setColor(Color.lightGray);
                    aCB.setStartValue(unDef);
                    aCB.setEndValue(aCB.getStartValue());
                    aCB.setCaption("NoData");
                    aCB.setNoData(true);
                    legendScheme.getLegendBreaks().add(aCB);
                    legendScheme.setHasNoData(true);
                }
                break;
        }

        return legendScheme;
    }

    /**
     * Create colors from start and end color
     *
     * @param sColor Start color
     * @param eColor End color
     * @param cNum Color number
     * @return Color array
     */
    public static Color[] createColors(Color sColor, Color eColor, int cNum) {
        Color[] colors = new Color[cNum];
        int sR, sG, sB, eR, eG, eB;
        int rStep, gStep, bStep;
        int i;

        sR = sColor.getRed();
        sG = sColor.getGreen();
        sB = sColor.getBlue();
        eR = eColor.getRed();
        eG = eColor.getGreen();
        eB = eColor.getBlue();
        rStep = (int) ((eR - sR) / cNum);
        gStep = (int) ((eG - sG) / cNum);
        bStep = (int) ((eB - sB) / cNum);
        for (i = 0; i < colors.length; i++) {
            colors[i] = new Color(sR + i * rStep, sG + i * gStep, sB + i * bStep);
        }

        return colors;
    }

    /**
     * Create contour values by interval
     *
     * @param min Miminum value
     * @param max Maximum value
     * @param interval Interval value
     * @return Value array
     */
    public static double[] createContourValuesInterval(double min, double max, double interval) {
        double[] cValues;
        int cNum = (int) ((max - min) / interval) + 1;
        int i;

        cValues = new double[cNum];
        for (i = 0; i < cNum; i++) {
            //cValues[i] = min + i * interval;
            cValues[i] = BigDecimalUtil.add(min, BigDecimalUtil.mul(i, interval));
        }

        return cValues;
    }

    /**
     * Create random colors
     *
     * @param cNum Color number
     * @return The random colors
     */
    public static Color[] createRandomColors(int cNum) {
        Color[] colors = new Color[cNum];
        int i;
        Random randomColor = new Random();

        for (i = 0; i < cNum; i++) {
            colors[i] = new Color(randomColor.nextInt(256),
                    randomColor.nextInt(256), randomColor.nextInt(256));
        }

        return colors;
    }

    /**
     * Create rainbow colors
     *
     * @param cNum Color number
     * @return Rainbow color array
     */
    public static Color[] createRainBowColors(int cNum) {
        if (cNum > 13) {
            //return getRainBowColors_HSL(cNum);
            return getRainBowColors_HSV(cNum);
        }

        List<Color> colorList = new ArrayList<Color>();

        colorList.add(new Color(160, 0, 200));
        colorList.add(new Color(110, 0, 220));
        colorList.add(new Color(30, 60, 255));
        colorList.add(new Color(0, 160, 255));
        colorList.add(new Color(0, 200, 200));
        colorList.add(new Color(0, 210, 140));
        colorList.add(new Color(0, 220, 0));
        colorList.add(new Color(160, 230, 50));
        colorList.add(new Color(230, 220, 50));
        colorList.add(new Color(230, 175, 45));
        colorList.add(new Color(240, 130, 40));
        colorList.add(new Color(250, 60, 60));
        colorList.add(new Color(240, 0, 130));

        switch (cNum) {
            case 12:
                colorList.remove(new Color(0, 210, 140));
                break;
            case 11:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                break;
            case 10:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                colorList.remove(new Color(230, 175, 45));
                break;
            case 9:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                colorList.remove(new Color(230, 175, 45));
                colorList.remove(new Color(160, 230, 50));
                break;
            case 8:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                colorList.remove(new Color(230, 175, 45));
                colorList.remove(new Color(160, 230, 50));
                colorList.remove(new Color(110, 0, 220));
                break;
            case 7:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                colorList.remove(new Color(230, 175, 45));
                colorList.remove(new Color(160, 230, 50));
                colorList.remove(new Color(110, 0, 220));
                colorList.remove(new Color(0, 200, 200));
                break;
            case 6:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                colorList.remove(new Color(230, 175, 45));
                colorList.remove(new Color(160, 230, 50));
                colorList.remove(new Color(110, 0, 220));
                colorList.remove(new Color(0, 200, 200));
                colorList.remove(new Color(240, 130, 40));
                break;
            case 5:
                colorList.remove(new Color(0, 210, 140));
                colorList.remove(new Color(30, 60, 255));
                colorList.remove(new Color(230, 175, 45));
                colorList.remove(new Color(160, 230, 50));
                colorList.remove(new Color(110, 0, 220));
                colorList.remove(new Color(0, 200, 200));
                colorList.remove(new Color(240, 130, 40));
                colorList.remove(new Color(160, 0, 200));
                break;
        }

        Color[] colors = new Color[cNum];
        for (int i = 0; i < cNum; i++) {
            colors[i] = colorList.get(i);
        }

        return colors;
    }

//        public static Color[] getRainBowColors_HSL(int cNum)
//        {
//            double delta = 1.0 / cNum;
//            Color[] colors = new Color[cNum];
//            int n = cNum - 1;
//            for (double i = 0; i < 1; i += delta)
//            {
//                if (n == -1) {
//                    break;
//                }
//
//                ColorUtil.HSL hsl = new ColorUtils.HSL(i, 0.5, 0.5);
//                colors[n] = ColorUtils.HSLToRGB(hsl);
//                n -= 1;
//            }
//
//            return colors;
//        }
    /**
     * Get rainbow color by HSV/HSB
     *
     * @param cNum Color number
     * @return Rainbow colors
     */
    public static Color[] getRainBowColors_HSV(int cNum) {
        double p = 360.0 / cNum;
        Color[] colors = new Color[cNum];
        for (int i = 0; i < cNum; i++) {
            colors[cNum - i - 1] = Color.getHSBColor((float) (i * p), 1.0f, 1.0f);
        }

        return colors;
    }

    /**
     * Create contour values by minimum and maximum values
     *
     * @param min Minimum value
     * @param max Maximum value
     * @return Contour values
     */
    public static double[] createContourValues(double min, double max) {
        double[] cValues;
        int i, cNum, aD, aE;
        double cDelt, range, newMin;
        String eStr;

        range = BigDecimalUtil.sub(max, min);
        if (range == 0.0) {
            cNum = 1;
            cValues = new double[1];
            cValues[0] = min;
            return cValues;
        }

        eStr = String.format("%1$E", range);
        aD = Integer.parseInt(eStr.substring(0, 1));
        aE = (int) Math.floor(Math.log10(range));
//        int idx = eStr.indexOf("E");
//        if (idx < 0) {
//            aE = 0;
//        } else {
//            aE = Integer.parseInt(eStr.substring(eStr.indexOf("E") + 1));
//        }
        if (aD > 5) {
            cDelt = Math.pow(10, aE);
            cNum = aD;
            //newMin = Convert.ToInt32((min + cDelt) / Math.Pow(10, aE)) * Math.Pow(10, aE);
            newMin = (int) (min / cDelt + 1) * cDelt;
        } else if (aD == 5) {
            cDelt = aD * Math.pow(10, aE - 1);
            cNum = 10;
            //newMin = Convert.ToInt32((min + cDelt) / Math.Pow(10, aE)) * Math.Pow(10, aE);
            newMin = (int) (min / cDelt + 1) * cDelt;
            cNum++;
        } else {
            //cDelt = aD * Math.pow(10, aE - 1);
            cDelt = BigDecimalUtil.pow(10, aE - 1);
            cDelt = BigDecimalUtil.mul(aD, cDelt);
            cNum = 10;
            //newMin = Convert.ToInt32((min + cDelt) / Math.Pow(10, aE - 1)) * Math.Pow(10, aE - 1);
            newMin = (int) (min / cDelt + 1) * cDelt;
        }

        if (newMin + (cNum - 1) * cDelt > max) {
            cNum -= 1;
        }
        cValues = new double[cNum];
        for (i = 0; i < cNum; i++) {
            //cValues[i] = newMin + i * cDelt;
            cValues[i] = BigDecimalUtil.add(newMin, BigDecimalUtil.mul(i, cDelt));
        }

        return cValues;
    }

    /**
     * Get contour values and colors from legend scheme
     *
     * @param aLS The legend scheme
     * @return
     */
    public static Object[] getContoursAndColors(LegendScheme aLS) {
        int i;
        double[] cValues;
        Color[] colors;
        if (aLS.getHasNoData()) {
            cValues = new double[aLS.getBreakNum() - 2];
            colors = new Color[aLS.getBreakNum() - 1];
        } else {
            cValues = new double[aLS.getBreakNum() - 1];
            colors = new Color[aLS.getBreakNum()];
        }
        switch (aLS.getShapeType()) {
            case Polygon:
                for (i = 0; i < aLS.getBreakNum(); i++) {
                    PolygonBreak aPGB = (PolygonBreak) aLS.getLegendBreaks().get(i);
                    colors[i] = aPGB.getColor();
                    if (i > 0) {
                        cValues[i - 1] = Double.parseDouble(aPGB.getStartValue().toString());
                    }
                }
                break;
            case Polyline:
                if (aLS.getLegendType() == LegendType.UniqueValue) {
                    cValues = new double[aLS.getBreakNum()];
                    colors = new Color[aLS.getBreakNum() + 1];
                    colors[0] = Color.white;
                    for (i = 0; i < aLS.getBreakNum(); i++) {
                        PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(i);
                        colors[i + 1] = aPLB.getColor();
                        cValues[i] = Double.parseDouble(aPLB.getStartValue().toString());
                    }
                } else {
                    for (i = 0; i < aLS.getBreakNum(); i++) {
                        PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(i);
                        colors[i] = aPLB.getColor();
                        if (i > 0) {
                            cValues[i - 1] = Double.parseDouble(aPLB.getStartValue().toString());
                        }
                    }
                }
                break;
            case Point:
                for (i = 0; i < aLS.getBreakNum(); i++) {
                    PointBreak aPB = (PointBreak) aLS.getLegendBreaks().get(i);
                    if (!aPB.isNoData()) {
                        colors[i] = aPB.getColor();
                        if (i > 0) {
                            cValues[i - 1] = Double.parseDouble(aPB.getStartValue().toString());
                        }
                    }
                }
                break;
        }

        return new Object[]{cValues, colors};
    }

    /**
     * Set contour values and colors from a legend scheme
     *
     * @param aLS Legend scheme
     * @param cValues Value array
     * @param colors Color array
     */
    public static void setContoursAndColors(LegendScheme aLS, double[] cValues, Color[] colors) {
        int i;
        if (aLS.getHasNoData()) {
            cValues = new double[aLS.getBreakNum() - 2];
            colors = new Color[aLS.getBreakNum() - 1];
        } else {
            cValues = new double[aLS.getBreakNum() - 1];
            colors = new Color[aLS.getBreakNum()];
        }
        switch (aLS.getShapeType()) {
            case Polygon:
                for (i = 0; i < aLS.getBreakNum(); i++) {
                    PolygonBreak aPGB = (PolygonBreak) aLS.getLegendBreaks().get(i);
                    colors[i] = aPGB.getColor();
                    if (i > 0) {
                        cValues[i - 1] = Double.parseDouble(aPGB.getStartValue().toString());
                    }
                }
                break;
            case Polyline:
                if (aLS.getLegendType() == LegendType.UniqueValue) {
                    cValues = new double[aLS.getBreakNum()];
                    colors = new Color[aLS.getBreakNum() + 1];
                    colors[0] = Color.white;
                    for (i = 0; i < aLS.getBreakNum(); i++) {
                        PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(i);
                        colors[i + 1] = aPLB.getColor();
                        cValues[i] = Double.parseDouble(aPLB.getStartValue().toString());
                    }
                } else {
                    for (i = 0; i < aLS.getBreakNum(); i++) {
                        PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(i);
                        colors[i] = aPLB.getColor();
                        if (i > 0) {
                            cValues[i - 1] = Double.parseDouble(aPLB.getStartValue().toString());
                        }
                    }
                }
                break;
            case Point:
                for (i = 0; i < aLS.getBreakNum(); i++) {
                    PointBreak aPB = (PointBreak) aLS.getLegendBreaks().get(i);
                    if (!aPB.isNoData()) {
                        colors[i] = aPB.getColor();
                        if (i > 0) {
                            cValues[i - 1] = Double.parseDouble(aPB.getStartValue().toString());
                        }
                    }
                }
                break;
        }
    }
}
