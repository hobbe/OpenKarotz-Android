package com.github.hobbe.android.openkarotz.karotz;

import junit.framework.TestCase;

import com.github.hobbe.android.openkarotz.karotz.IKarotz.EarPosition;

public class EarPositionTest extends TestCase {

    public void testFromAngle() {
        assertEquals(EarPosition.POSITION_1, EarPosition.fromAngle(0));
        assertEquals(EarPosition.POSITION_1, EarPosition.fromAngle(10));
        assertEquals(EarPosition.POSITION_2, EarPosition.fromAngle(20));
        assertEquals(EarPosition.POSITION_2, EarPosition.fromAngle(30));
        assertEquals(EarPosition.POSITION_3, EarPosition.fromAngle(40));
        assertEquals(EarPosition.POSITION_3, EarPosition.fromAngle(50));
        assertEquals(EarPosition.POSITION_4, EarPosition.fromAngle(60));
        assertEquals(EarPosition.POSITION_4, EarPosition.fromAngle(70));
        assertEquals(EarPosition.POSITION_5, EarPosition.fromAngle(80));
        assertEquals(EarPosition.POSITION_5, EarPosition.fromAngle(90));
        assertEquals(EarPosition.POSITION_5, EarPosition.fromAngle(100));
        assertEquals(EarPosition.POSITION_6, EarPosition.fromAngle(110));
        assertEquals(EarPosition.POSITION_6, EarPosition.fromAngle(120));
        assertEquals(EarPosition.POSITION_7, EarPosition.fromAngle(130));
        assertEquals(EarPosition.POSITION_7, EarPosition.fromAngle(140));
        assertEquals(EarPosition.POSITION_8, EarPosition.fromAngle(150));
        assertEquals(EarPosition.POSITION_8, EarPosition.fromAngle(160));
        assertEquals(EarPosition.POSITION_9, EarPosition.fromAngle(170));
        assertEquals(EarPosition.POSITION_9, EarPosition.fromAngle(180));
        assertEquals(EarPosition.POSITION_9, EarPosition.fromAngle(190));
        assertEquals(EarPosition.POSITION_10, EarPosition.fromAngle(200));
        assertEquals(EarPosition.POSITION_10, EarPosition.fromAngle(210));
        assertEquals(EarPosition.POSITION_11, EarPosition.fromAngle(220));
        assertEquals(EarPosition.POSITION_11, EarPosition.fromAngle(230));
        assertEquals(EarPosition.POSITION_12, EarPosition.fromAngle(240));
        assertEquals(EarPosition.POSITION_12, EarPosition.fromAngle(250));
        assertEquals(EarPosition.POSITION_13, EarPosition.fromAngle(260));
        assertEquals(EarPosition.POSITION_13, EarPosition.fromAngle(270));
        assertEquals(EarPosition.POSITION_13, EarPosition.fromAngle(280));
        assertEquals(EarPosition.POSITION_14, EarPosition.fromAngle(290));
        assertEquals(EarPosition.POSITION_14, EarPosition.fromAngle(300));
        assertEquals(EarPosition.POSITION_15, EarPosition.fromAngle(310));
        assertEquals(EarPosition.POSITION_15, EarPosition.fromAngle(320));
        assertEquals(EarPosition.POSITION_16, EarPosition.fromAngle(330));
        assertEquals(EarPosition.POSITION_16, EarPosition.fromAngle(340));
        assertEquals(EarPosition.POSITION_1, EarPosition.fromAngle(350));
        assertEquals(EarPosition.POSITION_1, EarPosition.fromAngle(360));

        assertEquals(EarPosition.POSITION_1, EarPosition.fromAngle(359));

        assertEquals(EarPosition.POSITION_2, EarPosition.fromAngle(390));
        assertEquals(EarPosition.POSITION_4, EarPosition.fromAngle(425));
        assertEquals(EarPosition.POSITION_7, EarPosition.fromAngle(500));

    }

    public void testFromIntValue() {
        assertEquals(EarPosition.POSITION_1, EarPosition.fromIntValue(1));
        assertEquals(EarPosition.POSITION_2, EarPosition.fromIntValue(2));
        assertEquals(EarPosition.POSITION_3, EarPosition.fromIntValue(3));
        assertEquals(EarPosition.POSITION_4, EarPosition.fromIntValue(4));
        assertEquals(EarPosition.POSITION_5, EarPosition.fromIntValue(5));
        assertEquals(EarPosition.POSITION_6, EarPosition.fromIntValue(6));
        assertEquals(EarPosition.POSITION_7, EarPosition.fromIntValue(7));
        assertEquals(EarPosition.POSITION_8, EarPosition.fromIntValue(8));
        assertEquals(EarPosition.POSITION_9, EarPosition.fromIntValue(9));
        assertEquals(EarPosition.POSITION_10, EarPosition.fromIntValue(10));
        assertEquals(EarPosition.POSITION_11, EarPosition.fromIntValue(11));
        assertEquals(EarPosition.POSITION_12, EarPosition.fromIntValue(12));
        assertEquals(EarPosition.POSITION_13, EarPosition.fromIntValue(13));
        assertEquals(EarPosition.POSITION_14, EarPosition.fromIntValue(14));
        assertEquals(EarPosition.POSITION_15, EarPosition.fromIntValue(15));
        assertEquals(EarPosition.POSITION_16, EarPosition.fromIntValue(16));
    }

}
