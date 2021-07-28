package com.example.musev1.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.musev1.db.MuseDB;
import com.example.musev1.db.MuseDao;
import com.example.musev1.model.AlertModel;
import com.example.musev1.model.AuthModel;
import com.example.musev1.model.DeviceModel;
import com.example.musev1.model.DeviceRequestModel;
import com.example.musev1.model.DeviceResponseModel;
import com.example.musev1.model.GoalModel;
import com.example.musev1.model.InsightModel;
import com.example.musev1.model.LoginResponseModel;
import com.example.musev1.model.ScheduleModel;
import com.example.musev1.network.ApiService;
import com.example.musev1.network.RetrofitBuilder;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class Repository {
    private ApiService apiService;
    private final MuseDB museDB;
    private final MuseDao museDao;

    public Repository(Application application) {
        this.museDB = MuseDB.getInstance(application);
        museDao = museDB.museDao();
    }

    //==========================================================================================//
    //API

    public Call<ResponseBody> register(AuthModel authModel) {
        apiService = RetrofitBuilder.getInstance().create(ApiService.class);
        return apiService.register(authModel);
    }

    public Call<LoginResponseModel> login(AuthModel authModel){
        apiService= RetrofitBuilder.getInstance().create(ApiService.class);
        return apiService.login(authModel);
    }

    public void setSecretToken(String token) {
        this.apiService = RetrofitBuilder.getAuthInstance(token).create(ApiService.class);
    }

    //________________________________________________________________________________________//
    //Alerts

    public Observable<List<AlertModel>> getAllAlertsRequest(){
        return apiService.getAllAlertsRequest();
    }

    public Observable<Call<List<AlertModel>>> getAlertsById(int deviceId){
        return apiService.getAlertsById(deviceId);
    }

    public Call<ResponseBody> deleteAlertById(int alertId){
        return apiService.deleteAlertById(alertId);
    }

    //________________________________________________________________________________________//
    //Custom Alerts

    public Observable<List<AlertModel>> getAllCustomAlertsRequest(){
        return apiService.getAllCustomAlertsRequest();
    }

    public Call<AlertModel> addCustomAlert(AlertModel scheduleModel){
        return apiService.addCustomAlert(scheduleModel);
    }

    public Observable<Call<List<AlertModel>>> getCustomAlertsById(int deviceId){
        return apiService.getCustomAlertsById(deviceId);
    }

    public Call<ResponseBody> deleteCustomAlertById(int customAlertId){
        return apiService.deleteCustomAlertById(customAlertId);
    }

    //________________________________________________________________________________________//
    //Devices

    public Call<DeviceResponseModel> addHouse(DeviceRequestModel requestModel) {
        return apiService.addHouse(requestModel);
    }

    public Observable<DeviceResponseModel> getHouse(){
        return apiService.getHouse();
    }

    public Call<DeviceResponseModel> addDevice(DeviceRequestModel requestModel){
        return apiService.addDevice(requestModel);
    }

    public Observable<List<DeviceRequestModel>> getAllDevicesRequest(int aggregation, int unit){
        return apiService.getAllDevicesRequest(aggregation,unit);
    }

    public Observable<DeviceResponseModel> getDeviceById(int deviceId){
        return apiService.getDeviceById(deviceId);
    }

    public Call<DeviceResponseModel> editDeviceById(int deviceId,DeviceRequestModel requestModel){
        return apiService.editDeviceById(deviceId,requestModel);
    }

    public Call<ResponseBody> deleteDeviceById(int deviceId){
        return apiService.deleteDeviceById(deviceId);
    }

    public Call<ResponseBody> setState(int deviceId, int state){
        return apiService.setState(deviceId,state);
    }

    //________________________________________________________________________________________//
    //Goals

    public Observable<List<GoalModel>> getAllGoalsRequest(){
        return apiService.getAllGoalsRequest();
    }

    public Call<GoalModel> addGoal(GoalModel goalModel){
        return apiService.addGoal(goalModel);
    }

    public Observable<Call<List<GoalModel>>> getGoalsById(int deviceId){
        return apiService.getGoalsById(deviceId);
    }

    public Call<ResponseBody> deleteGoalById(int goalId){
        return apiService.deleteGoalById(goalId);
    }

    //________________________________________________________________________________________//
    //Insights

    public Observable<InsightModel> getInsightRequest(int id, int aggregation, int unit){
        return apiService.getInsightRequest(id,aggregation,unit);
    }

    public Observable<Call<InsightModel>> getCustomInsightRequest(int id, String unit, String year
            , String month, String day){
        return apiService.getCustomInsightRequest(id,unit,year,month,day);
    }

    public Observable<ResponseBody> getCurrentUsageRequest(int id, String unit){
        return apiService.getCurrentUsageRequest(id,unit);
    }

    //________________________________________________________________________________________//
    //Schedules

    public Observable<List<ScheduleModel>> getAllSchedulesRequest(){
        return apiService.getAllSchedulesRequest();
    }

    public Call<ScheduleModel> addSchedule(ScheduleModel scheduleModel){
        return apiService.addSchedule(scheduleModel);
    }

    public Observable<Call<List<ScheduleModel>>> getSchedulesById(int deviceId){
        return apiService.getSchedulesById(deviceId);
    }

    public Call<ResponseBody> deleteScheduleById(int scheduleId){
        return apiService.deleteScheduleById(scheduleId);
    }

    //===========================================================================================//
    //Room

    public void insertDevice(DeviceModel device){
        new InsertDeviceAsyncTask(museDao).doInBackground(device);
    }

    public void updateDevice(DeviceModel device){
        new UpdateDeviceAsyncTask(museDao).doInBackground(device);
    }

    public void deleteDevice(DeviceModel device){
        new DeleteDeviceAsyncTask(museDao).doInBackground(device);
    }

    public LiveData<List<DeviceModel>> getAllDevices(){
        return museDao.getAllDevices();
    }

    public LiveData<List<DeviceModel>> getDevicesAdded(){
        return museDao.getDevicesAdded();
    }

    public LiveData<List<DeviceModel>> getDevicesGoals(){
        return museDao.getDevicesGoals();
    }

    public LiveData<List<DeviceModel>> getDevicesAlerts(){
        return museDao.getDevicesAlerts();
    }

    public LiveData<List<DeviceModel>> getDevicesSchedules() {
        return museDao.getDevicesSchedules();
    }

    public LiveData<List<DeviceModel>> getDevicesCustomAlerts() {
        return museDao.getDevicesCustomAlerts();
    }

    public LiveData<List<DeviceModel>> getDevicesWithoutGoal() {
        return museDao.getDevicesWithoutGoal();
    }

    public LiveData<List<DeviceModel>> getDevicesWithoutSchedule() {
        return museDao.getDevicesWithoutSchedule();
    }

    public LiveData<List<DeviceModel>> getDevicesWithoutCustomAlert() {
        return museDao.getDevicesWithoutCustomAlert();
    }

    public static class InsertDeviceAsyncTask extends AsyncTask<DeviceModel,Void,Void>{
        private final MuseDao museDao;

        public InsertDeviceAsyncTask(MuseDao museDao) {
            this.museDao = museDao;
        }

        @Override
        protected Void doInBackground(DeviceModel... deviceModels) {
            museDao.insertDevice(deviceModels[0]);
            return null;
        }
    }

    public static class UpdateDeviceAsyncTask extends AsyncTask<DeviceModel,Void,Void>{
        private final MuseDao museDao;

        public UpdateDeviceAsyncTask(MuseDao museDao) {
            this.museDao = museDao;
        }

        @Override
        protected Void doInBackground(DeviceModel... deviceModels) {
            museDao.updateDevice(deviceModels[0]);
            return null;
        }
    }

    public static class DeleteDeviceAsyncTask extends AsyncTask<DeviceModel,Void,Void>{
        private final MuseDao museDao;

        public DeleteDeviceAsyncTask(MuseDao museDao) {
            this.museDao = museDao;
        }

        @Override
        protected Void doInBackground(DeviceModel... deviceModels) {
            museDao.deleteDevice(deviceModels[0]);
            return null;
        }
    }
}