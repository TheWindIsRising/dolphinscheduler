package org.apache.dolphinscheduler.api.service;

import org.apache.dolphinscheduler.api.exceptions.NullException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;


/**
 * @Author jxz
 * @Date 2024/2/19 8:52
 * @Version v1.0
 **/
@Service
public class HarborService {
    private  final String harborHost = System.getenv("harborHost");
    private  final String harborPort = System.getenv("harborPort");

    /**
     * 获取项目地址
     * export function queryProjectsList():any{
     * return axios({
     * url: '/dsapi/v2.0/projects?page=1&page_size=100',
     * method: 'get',
     * baseURL:''
     * })
     * }
     */
    public JSONArray getKerberosProjectsList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("page", "1");
        paramMap.put("page_size", "2");
//        String url = "http://10.200.9.10:30880/dsapi/v2.0/projects";
        String url = "http://" + harborHost + ":" + harborPort + "/api/v2.0/projects";
        String body = HttpUtil.get(url, paramMap);
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONUtil.parseArray(body);
        } catch (JSONException e) {
            throw new NullException("接口异常，JSON转换失败：" + e.getMessage(), e);
        }
        return jsonArray;
    }

    /**
     * 获取镜像文件夹地址
     */
    public JSONArray getKerberosFileList(String project) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("project", project);
//        String url = "http://10.200.9.10:30880/dsapi/v2.0/projects/" + project + "/repositories";
        String url = "http://" + harborHost + ":" + harborPort + "/api/v2.0/projects/" + project + "/repositories";
        String body = HttpUtil.get(url, paramMap);
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONUtil.parseArray(body);
        } catch (JSONException e) {
            throw new NullException("接口异常,JSON转换失败，请检查参数：" + e.getMessage(), e);
        }
        return jsonArray;
    }

    public JSONArray getKerberosImageList(String project, String file) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("project", project);
        paramMap.put("file", file);
//        String url = "http://10.200.9.10:30880/dsapi/v2.0/projects/" + project + "/repositories/" + file + "/artifacts?with_tag=false&with_scan_overview=true&with_label=true&with_accessory=false";
        String url = "http://" + harborHost + ":" + harborPort + "/api/v2.0/projects/" + project + "/repositories/" + file + "/artifacts?with_tag=false&with_scan_overview=true&with_label=true&with_accessory=false";
        String body = HttpUtil.get(url, paramMap);
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONUtil.parseArray(body);
        } catch (JSONException e) {
            throw new NullException("接口异常,JSON转换失败，请检查参数：" + e.getMessage(), e);
        }
        return jsonArray;
    }

    public JSONArray getKerberosImageTag(String project, String path, String img) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("project", project);
        paramMap.put("file", path);
        paramMap.put("img", img);
//        String url = "http://10.200.9.10:30880/dsapi/v2.0/projects/" + project + "/repositories/" + path + "/artifacts/" + img + "/tags?with_signature=true&with_immutable_status=true&page_size=8&page=1";
        String url = "http://" + harborHost + ":" + harborPort + "/api/v2.0/projects/" + project + "/repositories/" + path + "/artifacts/" + img + "/tags?with_signature=true&with_immutable_status=true&page_size=8&page=1";
        String body = HttpUtil.get(url, paramMap);
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONUtil.parseArray(body);
        } catch (JSONException e) {
            throw new NullException("接口异常,JSON转换失败，请检查参数：" + e.getMessage(), e);
        }
        return jsonArray;
    }
}
