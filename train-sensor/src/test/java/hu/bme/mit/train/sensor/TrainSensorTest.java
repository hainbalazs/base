package hu.bme.mit.train.sensor;

import hu.bme.mit.train.interfaces.TrainController;
import hu.bme.mit.train.interfaces.TrainSensor;
import hu.bme.mit.train.interfaces.TrainUser;
import hu.bme.mit.train.user.TrainUserImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class TrainSensorTest {

    private TrainController mockTC;
    private TrainUser mockTU;
    private TrainSensor ts;

    @Before
    public void init() {
        mockTC = mock(TrainController.class);
        mockTU = mock(TrainUser.class);
        ts =  new TrainSensorImpl(mockTC, mockTU);
    }

    @Test
    public void overrideSpeedLimit_under0_alarm() {
        /* intialize the speedlimit for this test-case to see only the absolute margin applies
         * need to call it twice since 5 is the default value for speedLimit
         * and if it gets overridden by 1 once we get into alarmState since it is > 50% decrease */
        ts.overrideSpeedLimit(1);
        ts.overrideSpeedLimit(1);
        ts.overrideSpeedLimit(-1);
        verify(mockTC, times(1)).setSpeedLimit(-1);
        verify(mockTU, times(2)).setAlarmState(true);
        verify(mockTU, times(1)).setAlarmState(false);
        when(mockTU.getAlarmState()).thenReturn(true);
    }

    @Test
    public void overrideSpeedLimit_above500_alarm() {
        ts.overrideSpeedLimit(501);
        verify(mockTC, times(1)).setSpeedLimit(501);
        verify(mockTU, times(1)).setAlarmState(true);
        when(mockTU.getAlarmState()).thenReturn(true);
    }

    @Test
    public void overrideSpeedLimit_moreThan50PercentDecrease_alarm() {
        ts.overrideSpeedLimit(150);
        ts.overrideSpeedLimit(74);
        verify(mockTC, times(1)).setSpeedLimit(74);
        verify(mockTU, times(1)).setAlarmState(true);
        when(mockTU.getAlarmState()).thenReturn(true);
    }

    @Test
    public void overrideSpeedLimit_lessThan50PercentDecrease_normal(){
        ts.overrideSpeedLimit(150);
        ts.overrideSpeedLimit(76);
        verify(mockTC, times(1)).setSpeedLimit(76);
        verify(mockTU, times(0)).setAlarmState(true);
        when(mockTU.getAlarmState()).thenReturn(false);
    }
}
