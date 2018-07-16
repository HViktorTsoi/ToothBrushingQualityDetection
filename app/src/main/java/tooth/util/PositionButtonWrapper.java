package tooth.util;

import android.widget.Button;
/*
    存储button信息
    Created by Cui Jiahe 2018/07/16
 */
public class PositionButtonWrapper {

    final static private String[] labelList = {
            "",
            "无", "漱口", "上左后外",
            "上前外", "上右后外", "上左后中",
            "上右后中", "上左后内", "上前内",
            "上右后内", "下左后外", "下前外",
            "下右后外", "下左后中", "下右后中"
            , "下左后内", "下前内", "下右后内"
    };
    private Button button;
    private Integer buttonLogicID;

    public PositionButtonWrapper(Button button, Integer buttonLogicID) {
        this.button = button;
        this.buttonLogicID = buttonLogicID;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public Integer getButtonLogicID() {
        return buttonLogicID;
    }

    public void setButtonLogicID(Integer buttonLogicID) {
        this.buttonLogicID = buttonLogicID;
    }

    public String getLabel() {
        return labelList[buttonLogicID];
    }
}
