package org.apache.dolphinscheduler.server.worker.utils;


import org.apache.dolphinscheduler.plugin.task.api.enums.TaskExecutionStatus;
import org.apache.dolphinscheduler.remote.utils.Host;
import org.apache.dolphinscheduler.service.storage.impl.HadoopUtils;
import org.slf4j.Logger;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.List;

public class YarnAPi {

    /**
     * @param host   主机
     * @param appIds applocations
     * @param logger 日志
     */

//    @Value("${yarnHostName}")
//    private static String yarnHostName;
    public static void cancelApplication(Host host, List<String> appIds, Logger logger) {
        if (appIds == null || appIds.isEmpty()) {
            return;
        }

        for (String appId : appIds) {
            try {
                TaskExecutionStatus applicationStatus = HadoopUtils.getInstance().getApplicationStatus(appId);

                if (!applicationStatus.isFinished()) {
                    String filePath = "/opt/soft/resourcemanager.txt";
                    BufferedReader reader = new BufferedReader(new FileReader(filePath));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 处理每一行的值
                        logger.info("=====================line：====" + line + "==========================");
                    }
//                    logger.info("=====================yarnHostName：====" + yarnHostName + "==========================");
                    String url = "http://172.16.30.216:8088/ws/v1/cluster/apps/" + appId + "/state?user.name=hdfs";
                    logger.info("======================url:====" + url + "==========================");
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
                    logger.info("=========================目前任务状态为：" + status);
                    if (!status.equals("FAILED") && !status.equals("KILLED")) {
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
                        logger.info("=========================调用Yarn Api修改任务状态为：" + map.toString());
                    }
                }
            } catch (Exception e) {
                logger.error("Get yarn application app id [{}}] status failed", appId, e);
            }
        }
    }

}
