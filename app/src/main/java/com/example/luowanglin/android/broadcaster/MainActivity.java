package com.example.luowanglin.android.broadcaster;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    AudioTrackManager audio;
    boolean isPlaySound;
//    Thread thread;
    OptionsPickerView pvOption;
    @BindView(R.id.contrBtn) Button controlBtn;
    @BindView(R.id.hourBtn) Button hourBtn;
    @BindView(R.id.minuteBtn) Button minuteBtn;
    @BindView(R.id.secondBtn) Button secondBtn;
    @BindView(R.id.submitBtn) Button submitBtn;
    @BindView(R.id.timenumber) EditText timeNum;
    boolean isOFF = true;
    int flagCount = 0;

    int testNum = 3234;

    HashMap<String,Double> mapHZ = new HashMap<String,Double>(){{
        put("STX",523.251);
        put("0",1760.000);
        put("1",1864.655);
        put("2",1975.533);
        put("3",2093.005);
        put("4",2217.461);
        put("5",2349.318);
        put("6",2489.016);
        put("7",2637.021);
        put("8",2793.826);
        put("9",2959.956);
        put("O",3135.964);
        put("F",3322.438);
    }};

    List<Double> exeHZ = new ArrayList<Double>();

    List<String> hourItems = new ArrayList<String>() {{
        for (Integer i = 0;i < 24;i++){
            String result = null;
            if (i < 10){
                result = 0 + "" + i;
            }else {
                result = i + "";
            }
            add(result);
        }
    }};

    List<String> minAndSecItems = new ArrayList<String>(){{
        for (Integer i = 0;i < 60;i++){
            String result = null;
            if (i < 10){
                result = 0 + "" + i;
            }else {
                result = i + "";
            }
            add(result);
        }
    }};

    List<List<String>> minuItems = new ArrayList<List<String>>(){{
        for (Integer i = 0;i < 60;i++){
            add(minAndSecItems);
        }
    }};

    List<List<List<String>>> secItems = new ArrayList<List<List<String>>>(){{
        for (Integer i = 0;i < 60;i++){
            add(minuItems);
        }
    }};

    TimerTask task = null;
    Timer timer = null;
    long silencPeriod = 40;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        submitBtn.setSoundEffectsEnabled(false);
        audio=new AudioTrackManager();

        //时间选择器
        initForOptionPicker();

        timeNum.setCursorVisible(false);
        timeNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //隐藏光标
                timeNum.setCursorVisible(false);
                String textStr = textView.getText().toString();

                if (textStr.length() != 0){

                    silencPeriod = Long.parseLong(textStr);

                }else {
                    //编辑区域为空
                    silencPeriod = 40;
                }

                return false;
            }
        });

    }

    //Time picker
    void initForOptionPicker(){
        pvOption = new  OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                //返回的分别是三个级别的选中位置
                if (exeHZ.size() > 0){
                    exeHZ.removeAll(exeHZ);
                }

                exeHZ.add(mapHZ.get("STX"));
                exeHZ.add(mapHZ.get(hourItems.get(options1).substring(0,1)));
                exeHZ.add(mapHZ.get(hourItems.get(options1).substring(1,2)));
                exeHZ.add(mapHZ.get(minuItems.get(options1).get(option2).substring(0,1)));
                exeHZ.add(mapHZ.get(minuItems.get(options1).get(option2).substring(1,2)));
                exeHZ.add(mapHZ.get(secItems.get(options1).get(option2).get(options3).substring(0,1)));
                exeHZ.add(mapHZ.get(secItems.get(options1).get(option2).get(options3).substring(1,2)));

                if (isOFF){
                    exeHZ.add(mapHZ.get("O"));
                }else {
                    exeHZ.add(mapHZ.get("F"));
                }

                int sum = Integer.parseInt(hourItems.get(options1).substring(0,1)) + Integer.parseInt(hourItems.get(options1).substring(1,2));
                sum += Integer.parseInt(minuItems.get(options1).get(option2).substring(0,1)) + Integer.parseInt(minuItems.get(options1).get(option2).substring(1,2));
                sum += Integer.parseInt(secItems.get(options1).get(option2).get(options3).substring(0,1)) + Integer.parseInt(secItems.get(options1).get(option2).get(options3).substring(1,2));

                int result = 0;
                while (sum != 0){
                    result += sum % 10;
                    sum /= 10;
                }

                exeHZ.add(mapHZ.get(result+""));
                hourBtn.setText(hourItems.get(options1));
                minuteBtn.setText(minuItems.get(options1).get(option2));
                secondBtn.setText(secItems.get(options1).get(option2).get(options3));

            }
        }).setSubmitText("确定")//确定按钮文字
                .setCancelText("取消")//取消按钮文字
                .setTitleText("时间选择")//标题
                .setCancelColor(getResources().getColor(R.color.colorGreen))
                .setSubmitColor(getResources().getColor(R.color.colorGreen))
                .setTitleColor(getResources().getColor(R.color.colorGray))
                .setTextColorCenter(getResources().getColor(R.color.colorGray))
                .setSubCalSize(16)//确定和取消文字大小
                .setTitleSize(18)//标题文字大小
                .setContentTextSize(18)//滚轮文字大小
                .setLinkage(true)//设置是否联动，默认true
                .setLabels("时", "分", "秒")//设置选择的三级单位
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCyclic(false, false, false)//循环与否
                .setSelectOptions(0, 0, 0)  //设置默认选中项
                .setOutSideCancelable(true)//点击外部dismiss default true、
                .build();

        pvOption.setPicker(hourItems, minuItems, secItems);
        pvOption.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            isPlaySound=false;
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.secondBtn,R.id.minuteBtn,R.id.hourBtn}) public void showPickerTime() {
        //隐藏edittext软盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(timeNum.getWindowToken(), 0);
        }
        //展示时间选择器
        pvOption.show();
        isPlaySound = false;
    }

    @OnClick(R.id.contrBtn) void controlAction(){
        //
        isOFF = !isOFF;
        if (isOFF){
            controlBtn.setText("开");
            controlBtn.setTextColor(getResources().getColor(R.color.colorGreen));
            if (exeHZ.size() > 0){
                exeHZ.set(7,mapHZ.get("O"));
            }
        }else {
            controlBtn.setText("关");
            controlBtn.setTextColor(getResources().getColor(R.color.colorYellow));
            if (exeHZ.size() > 0){
                exeHZ.set(7,mapHZ.get("F"));
            }
        }

    }

    @OnClick(R.id.submitBtn) void submitAction(){

        stopTimer();

        //execute submint parameter
        if (exeHZ.size() == 0){
            Toast.makeText(getApplicationContext(), "参数为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPlaySound) {
            flagCount = 0;
            isPlaySound = true;
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
                    startTimer();
//                }
//            };
//            thread.start();
        }
    }

    @OnClick(R.id.timenumber) void editStart(){
        //显示光标
        timeNum.setCursorVisible(true);
    }

    private void startTimer(){
        if (timer == null){
            timer = new Timer();
        }

        if (task == null){
            task = new TimerTask() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */

                    audio.stop();
                    System.out.println("..........."+flagCount);
                    if (flagCount / 2 == 9){
                        System.out.println("结束了。。。。。over");
                        isPlaySound = false;
                        stopTimer();
                        audio.stop();
                        flagCount -= 1;
                        return;
                    }
                    if (flagCount % 2 != 0){
                        flagCount ++;
                        return;
                    }

                    int hz = exeHZ.get(flagCount/2).intValue();
                    audio.stop();
                    audio.start(hz);
                    audio.play();
                    System.out.println("...hz.." + hz +"...flagcount..." + flagCount);
                    flagCount ++;
                }
            };
        }

        if (timer != null && task != null){
//            timer.schedule(task,0,40);
            timer.scheduleAtFixedRate(task,0,silencPeriod);
        }
    }

    private void stopTimer(){

        isPlaySound = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }




}

