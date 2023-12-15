package org.apache.dolphinscheduler.server.worker.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
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

public class YarnAPi {

    /**
     * @param host   主机
     * @param appIds applocations
     * @param logger 日志
     */

    public static void cancelApplication(Host host, List<String> appIds, Logger logger) {
        String yarnHostName = System.getenv("yarnHostName");
        String principal = System.getenv("principalName");
        String serviceCluster = System.getenv("serviceCluster");
        String cookie = System.getenv("cookie"); //JSESSIONID=1xvqyk3z2z3qi1slj01ehf7l6w

        if (appIds == null || appIds.isEmpty()) {
            logger.error("==============================appIds为空=================================");
            return;
        }

        for (String appId : appIds) {
            try {
                logger.error("==============================appIds不为空=================================");
                TaskExecutionStatus applicationStatus = HadoopUtils.getInstance().getApplicationStatus(appId);
                if (!applicationStatus.isFinished()) {
                    logger.info("=====================yarnHostName：" + yarnHostName + "==========================");
                    String url = yarnHostName + appId + "/state?user.name=" + principal;
                    logger.info("======================url：" + url + "==========================");
                    //开关 区分华为FI 与ambari
                    switch (serviceCluster) {
                        case "huawei":
                            handleHuaweiServiceCluster(url, logger, cookie);
                            break;
                        case "ambari":
                            handleAmbariServiceCluster(url, logger);
                            break;
                        default:
                            logger.error("获取不到大数据平台名称");
                            break;
                    }
                }
            } catch (Exception e) {
                logger.error("Get yarn application app id [{}] status failed", appId, e);
            }
        }
    }

    private static void handleHuaweiServiceCluster(String url, Logger logger, String cookie) {
        String result = HttpUtil
                .createGet(url)
                .header("Cookie", cookie)
                .execute()
                .charset("utf-8")
                .body();
        logger.info("=====================result：" + result);
        if (StrUtil.isNotBlank(result)) {
            Map<String, Object> resultMap = JSONUtil.parseObj(result);
            String status = resultMap.get("state").toString();
            logger.info("=====================任务状态为：" + status);
            if (!status.equals("FAILED") && !status.equals("KILLED")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("state", "KILLED");
                String string = jsonObject.toString();
                String update = HttpRequest.put(url)
                        .header("Cookie", cookie)
                        .body(string)
                        .execute()
                        .body();
                Map<String, Object> map = JSONUtil.parseObj(update);
                String str = map.get("state").toString();
                if (StrUtil.isNotBlank(str)) {
                    logger.info("=====================任务状态修改成功=====================");
                }
            }
        } else {
            logger.error("请检擦参数或cookie失效");
        }
    }

    private static void handleAmbariServiceCluster(String url, Logger logger) {
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
            logger.info("=========================调用Yarn Api修改任务状态为：" + map);
        }
    }

}
