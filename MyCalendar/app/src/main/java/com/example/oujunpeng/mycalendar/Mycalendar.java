package com.example.oujunpeng.mycalendar;

import android.content.Context;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Mycalendar extends LinearLayout {

    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    //获取当前日期
    private Calendar curDate= Calendar.getInstance();
    private String displayFormat;
    //长按事件
    public MyCalendarListener listener;
    //完整的头部显示
    public Mycalendar(Context context) {
        super(context);
    }
    public Mycalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context,attrs);
    }
    public Mycalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context,attrs);
    }


    private void initControl(Context context,AttributeSet attrs){
        bindControl(context);
        bindControlEvent();

        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.Mycalendar);

        try {
            String format =ta.getString(R.styleable.Mycalendar_dateFormat);
            displayFormat=format;
            if(displayFormat==null){
                displayFormat="MMM yyyy";
            }
        }finally {
            ta.recycle();
        }

        renderCalendar();

    }

    private void bindControl(Context context){
        //绑定控件
        LayoutInflater inflater=LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view,this);
        btnPrev=(ImageView)findViewById(R.id.btnPrev);
        btnNext=(ImageView)findViewById(R.id.btnNext);
        txtDate=(TextView)findViewById(R.id.txtDate);
        grid=(GridView)findViewById(R.id.calendar_gird);
    }

    private void bindControlEvent(){
        //绑定事件
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curDate.add(Calendar.MONTH,-1);
                renderCalendar();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                curDate.add(Calendar.MONTH,1);
                renderCalendar();
            }
        });
    }

    private void renderCalendar(){
        SimpleDateFormat sdf = new SimpleDateFormat(displayFormat);
        txtDate.setText(sdf.format(curDate.getTime()));

        ArrayList<Date> cells =new ArrayList<>();
        Calendar calendar=(Calendar) curDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH,1);
        int prevDays =calendar.get(Calendar.DAY_OF_WEEK)-1;
        calendar.add(Calendar.DAY_OF_MONTH,-prevDays);

        int maxCellCount=6*7;
        while(cells.size()<maxCellCount){
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }

        grid.setAdapter(new CalendarAdapter(getContext(),cells));
        //长按事件
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener==null){
                    return false;
                }else {
                    listener.onItemLongPress((Date) parent.getItemAtPosition(position));
                    return true;
                }
            }
        });
    }

    private class CalendarAdapter extends ArrayAdapter<Date>{
        LayoutInflater inflater;


        public CalendarAdapter(Context context,ArrayList<Date> days) {
            super(context, R.layout.calendar_text_day,days);
            inflater=LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Date date=getItem(position);

            if(convertView==null){
                convertView = inflater.inflate(R.layout.calendar_text_day,parent,false);
            }

            int day =date.getDate();
            ((TextView)convertView).setText(String.valueOf(day));

            Date today=new Date();
            boolean isTheSameMonth=false;
            if(date.getMonth()==today.getMonth()){
                isTheSameMonth=true;
            }
            if(isTheSameMonth){
                ((TextView)convertView).setTextColor(Color.parseColor("#000000"));
            }
            else{
                ((TextView)convertView).setTextColor(Color.parseColor("#666666"));
            }

            if (today.getDate()==date.getDate()&&today.getMonth()==date.getMonth()&&today.getYear()==date.getYear()){
                ((TextView)convertView).setTextColor(Color.parseColor("#ff0000"));
                ((Calendar_day_textView)convertView).isToday=true;
            }

            return convertView;
        }
    }

    //长按事件
    public interface MyCalendarListener{
        void  onItemLongPress(Date day);
    }

}
