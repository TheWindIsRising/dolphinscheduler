package org.apache.dolphinscheduler.server.worker.utils;

import org.apache.dolphinscheduler.plugin.task.api.enums.TaskExecutionStatus;
import org.apache.dolphinscheduler.remote.utils.Host;
import org.apache.dolphinscheduler.service.storage.impl.HadoopUtils;
import org.slf4j.Logger;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Map;
import java.util.List;
import java.util.Map;

/**
 * @Author jxz
 * @Date 2023/11/7 10:59
 * @Version v1.0
 **/
public class YarnAPi {
    public static void cancelApplication(Host host, List<String> appIds, Logger logger) {
        if (appIds == null || appIds.isEmpty()) {
            return;
        }

        for (String appId : appIds) {
            try {
                TaskExecutionStatus applicationStatus = HadoopUtils.getInstance().getApplicationStatus(appId);

                if (!applicationStatus.isFinished()) {
                    System.setProperty("HADOOP_USER_NAME", "hdfs");
                    String ip = host.getIp();
                    String url = "http://" + ip + ":8088/ws/v1/cluster/apps/" + appId + "/state";
                    logger.info("========================="+url+"==========================");
                    //查询状态
                    HttpRequest request = HttpRequest.get(url);
                    // 发送请求并获取响应结果
                    HttpResponse response = request.execute();
                    // 将响应结果转换为JSON字符串
                    String jsonStr = response.body();
                    // 将JSON字符串转换为Map对象，方便后续处理
                    Map<String, Object> resultMap = JSONUtil.parseObj(jsonStr);
                    // 输出结果
                    String status = resultMap.get("state").toString();
                    if(!status.equals("FAILED") && !status.equals("KILLED")){
                        HttpRequest httpRequest = HttpRequest.put(url);
                        // 发送请求并获取响应结果
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("state", "KILLED");
                        String string = jsonObject.toString();
                        httpRequest.body(string);
                        HttpResponse res = httpRequest.execute();
                        // 将响应结果转换为JSON字符串
                        String str = res.body();
                        // 将JSON字符串转换为Map对象，方便后续处理
                        Map<String, Object> map = JSONUtil.parseObj(str);
                        // 输出结果
                        logger.info("kill cmd:{}", map.toString());
                    }
                }
            } catch (Exception e) {
                logger.error("Get yarn application app id [{}}] status failed", appId, e);
            }
        }
    }

}
