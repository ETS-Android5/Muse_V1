package com.example.muse.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.muse.MainActivity;
import com.example.muse.R;
import com.example.muse.model.GoalModel;
import com.example.muse.model.InsightDataModel;
import com.example.muse.utility.SaveState;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HomeFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    private ChipNavigationBar chipNavigationBar;
    private NavController navControllerChart;
    private ImageView iv_custom;
    private int day, month, year;
    private DatePickerDialog datePickerDialog;
    private ConstraintLayout constLayout_expand;
    private CardView cv_insight;
    private ImageView iv_arrow;
    private Spinner spinner,spinner_agg;
    private TextView tv_current, tv_average, tv_per, tv_consumedV, tv_estimation,tv_used,tv_prediction,tv_percent;
    private ProgressBar progressBar;
    private int aggregation = -1;
    private boolean notViewed=true;
    public static ArrayList<InsightDataModel> dataModels=new ArrayList<>();
    public ArrayList<GoalModel> goalModels=new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navControllerChart = Navigation.findNavController(requireActivity(), R.id.FHome_fragment);
        navControllerChart.navigate(R.id.chartDayFragment);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //StatusBar color
        if (SaveState.getDarkModeState())
            MainActivity.setupBackgroundStatusBar(getResources().getColor(R.color.nice_black, null));
        else
            MainActivity.setupLightStatusBar(getResources().getColor(R.color.white_muse, null));

        //iv_custom
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        iv_custom = view.findViewById(R.id.FHome_iv_custom);
        iv_custom.setOnClickListener(this::showPopup);

        //spinner insight
        spinner = view.findViewById(R.id.home_spinner_aggregation);
        tv_current = view.findViewById(R.id.home_tv_currentV);
        tv_average = view.findViewById(R.id.home_tv_averageV);
        tv_consumedV = view.findViewById(R.id.home_tv_consumedV);
        tv_estimation = view.findViewById(R.id.home_tv_estV);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getHouseInfo(aggregation, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner agg
        spinner_agg = view.findViewById(R.id.home_spinner_unit);
        tv_used = view.findViewById(R.id.home_tv_goalUsed);
        tv_prediction = view.findViewById(R.id.home_tv_estimation);
        tv_percent = view.findViewById(R.id.home_tv_percent);
        progressBar = view.findViewById(R.id.home_pb);

        //default info
        aggregation = 1;
        getHouseInfo(1, 0);

        spinner_agg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (GoalModel model:goalModels){
                    if(model.getType()==position){
                        tv_used.setText(model.getUsed());
                        if(model.getEstimation()==0)
                            tv_prediction.setText("Goal will not achieve");
                        else
                            tv_prediction.setText("Goal will achieve");
                        tv_percent.setText(model.getPercent()+"");
                        progressBar.setProgress(model.getPercent());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //chipNav
        chipNavigationBar = view.findViewById(R.id.FHome_chipNav);
        tv_per = view.findViewById(R.id.home_tv_per);
        chipNavigationBar.setItemSelected(R.id.dayFragment, true);
        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.dayFragment:
                    navControllerChart.popBackStack();
                    navControllerChart.popBackStack();
                    navControllerChart.navigate(R.id.chartDayFragment);
                    tv_per.setText("Per day");
                    aggregation = 1;
                    getHouseInfo(1, spinner.getSelectedItemPosition());
                    return;

                case R.id.weekFragment:
                    navControllerChart.popBackStack();
                    navControllerChart.popBackStack();
                    navControllerChart.navigate(R.id.chartWeekFragment);
                    tv_per.setText("Per week");
                    aggregation = 2;
                    getHouseInfo(2, spinner.getSelectedItemPosition());
                    return;

                case R.id.monthFragment:
                    navControllerChart.popBackStack();
                    navControllerChart.popBackStack();
                    navControllerChart.navigate(R.id.chartMonthFragment);
                    tv_per.setText("Per month");
                    aggregation = 3;
                    getHouseInfo(3, spinner.getSelectedItemPosition());
                    return;

                case R.id.yearFragment:
                    navControllerChart.popBackStack();
                    navControllerChart.popBackStack();
                    navControllerChart.navigate(R.id.chartYearFragment);
                    tv_per.setText("Per year");
                    aggregation = 4;
                    getHouseInfo(4, spinner.getSelectedItemPosition());
            }
        });

        // arrow info
        constLayout_expand=view.findViewById(R.id.constLayoutExpanded);
        iv_arrow=view.findViewById(R.id.home_iv_arrow);
        cv_insight=view.findViewById(R.id.home_cv_insight);
        iv_arrow.setOnClickListener(v -> {
            if (constLayout_expand.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(cv_insight, new AutoTransition());
                constLayout_expand.setVisibility(View.VISIBLE);
                iv_arrow.setBackgroundResource(R.drawable.ic_arrow_up);
            } else {
                TransitionManager.beginDelayedTransition(cv_insight, new Fade());
                constLayout_expand.setVisibility(View.GONE);
                iv_arrow.setBackgroundResource(R.drawable.ic_arrow_down);
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.inflate(R.menu.menu_custom);
        popupMenu.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuCustom_day:
                displayDatePicker(0);
                return true;

            case R.id.menuCustom_month:
                displayDatePicker(1);
                datePickerDialog.findViewById(Resources.getSystem()
                        .getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                return true;

            case R.id.menuCustom_year:
                displayDatePicker(2);
                datePickerDialog.findViewById(Resources.getSystem()
                        .getIdentifier("day", "id", "android")).setVisibility(View.GONE);

                datePickerDialog.findViewById(Resources.getSystem()
                        .getIdentifier("month", "id", "android")).setVisibility(View.GONE);
                return true;

            default:
                return false;
        }
    }

    public void displayDatePicker(int position) {
        DatePickerDialog.OnDateSetListener onDateSetListener= (view, year, month, dayOfMonth) -> {
            if (position == 0) {
                Toast.makeText(getContext(), dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
            } else if (position == 1) {
                Toast.makeText(getContext(), (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), year + "", Toast.LENGTH_SHORT).show();
            }
        };
        datePickerDialog = new DatePickerDialog(getContext()
                , android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackground(null);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackground(null);
    }

    @SuppressLint("SetTextI18n")
    public void getHouseInfo(int agg, int spinnerItem) {
        MainActivity.displayLoadingDialog();
        MainActivity.museViewModel.getHouse()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            goalModels=result.getGoals();
                            for (GoalModel model:result.getGoals()){
                                if(model.getType()==spinner_agg.getSelectedItemPosition()){
                                    tv_used.setText(model.getUsed());
                                    if(model.getEstimation()==0)
                                        tv_prediction.setText("Goal will not achieve");
                                    else
                                        tv_prediction.setText("Goal will achieve");
                                    tv_percent.setText(model.getPercent()+"");
                                    progressBar.setProgress(model.getPercent());
                                    break;
                                }
                            }
                            MainActivity.museViewModel.getInsightRequest(result.getId(), agg, spinnerItem)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result1 -> {
                                                dataModels.clear();
                                                dataModels = result1.getData();
                                                tv_average.setText(result1.getAverageUsage() + "");
                                                tv_consumedV.setText(result1.getUsage() + "");
                                                tv_estimation.setText(result1.getEstimatedUsage() + "");

                                                MainActivity.museViewModel.getCurrentUsageRequest(result.getId(), spinnerItem + "")
                                                        .subscribeOn(Schedulers.computation())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(result2 -> {
                                                            tv_current.setText(result2.string());
                                                            MainActivity.progressDialog.dismiss();
                                                        }, error -> {
                                                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                                            MainActivity.progressDialog.dismiss();
                                                        });
                                                if (notViewed) {
                                                    navControllerChart.popBackStack();
                                                    navControllerChart.popBackStack();
                                                    navControllerChart.navigate(R.id.chartDayFragment);
                                                    notViewed = false;
                                                }
                                            },
                                            error -> {
                                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                                MainActivity.progressDialog.dismiss();
                                            });
                        },
                        error -> {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            MainActivity.progressDialog.dismiss();
                        });
    }
}