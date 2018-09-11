package tooth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.apache.commons.math3.stat.StatUtils;

import java.util.Objects;

import cjh.recorder.R;
import tooth.util.Constants;
import tooth.util.PositionButtonWrapper;

/**
 * Created by JX on 2017/11/29.
 */

public class BrushResultActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TextView totaltime, scrores;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brushresult_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        double[] timeAtPosition = bundle.getDoubleArray(Constants.TOTAL_TIME);
        boolean[] finished = bundle.getBooleanArray(Constants.FINISHED);
        double brushingTime = bundle.getDouble(Constants.BRUSHING_TIME);

        totaltime = (TextView) findViewById(R.id.brushresult_totaltime);
        scrores = (TextView) findViewById(R.id.brushresult_grade);
        TextView txtQualitified = (TextView) findViewById(R.id.quatified_brushresult);
        TextView txtUnQualitified = (TextView) findViewById(R.id.unquatified_brushresult);
        txtQualitified.setMovementMethod(ScrollingMovementMethod.getInstance());
        txtUnQualitified.setMovementMethod(ScrollingMovementMethod.getInstance());

//        totaltime.setText(timeFormat(total_time));
//        scrores.setText(scores + getResources().getString(R.string.brushresult_fen));
        txtQualitified.setText(buildStringResult(finished, timeAtPosition, true));
        txtUnQualitified.setText(buildStringResult(finished, timeAtPosition, false));

        scrores.setText(String.format("得分: %.1f分", countingScores(finished, timeAtPosition)));
        totaltime.setText("总时间: " + timeFormat((int) brushingTime / 1000));
    }


    private String timeFormat(int timeSeconds) {
        String result = "";

        if (timeSeconds >= 60) {
            result = timeSeconds / 60 + "m" + timeSeconds % 60 + "s";
        } else {
            result = timeSeconds + "s";
        }
        return result;
    }

    private String buildStringResult(boolean[] finished, double[] timeAtPosition, boolean qualified) {
        StringBuffer result = new StringBuffer();
        for (int i = 3; i < finished.length; ++i) {
            if (finished[i] == qualified) {
                result.append(String.format("%s:  %.1fs\n", PositionButtonWrapper.labelList[i], timeAtPosition[i]));
            }
        }
        if (result.length() == 0) {
            return "无";
        } else {
            return result.toString();
        }
    }

    private double countingScores(boolean[] finished, double[] timeAtPosition) {
        double sum = 0;
        double threshold = 15;
        for (int i = 3; i < finished.length; ++i) {
            if (finished[i]) {
                sum += (timeAtPosition[i] > 15 ? 15 : timeAtPosition[i]);
            } else {
                sum += 0 * timeAtPosition[i];
            }
        }
        return sum * 100 / (5 * 16);
    }

}
