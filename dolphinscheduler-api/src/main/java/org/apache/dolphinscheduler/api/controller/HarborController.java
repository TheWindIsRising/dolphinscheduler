package org.apache.dolphinscheduler.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dolphinscheduler.api.service.HarborService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author jxz
 * @Date 2024/2/19 9:05
 * @Version v1.0
 **/
@Api(tags = "HarborInterface")
@RestController
@RequestMapping("/api/harbor")
public class HarborController {
    @Resource
    private HarborService harborService;

    @ApiOperation(value = "获取仓库列表", notes = "获取仓库列表", response = Result.class)
    @GetMapping("/getProjectsList")
    public Result getProjectsList() {
        return Result.success(harborService.getKerberosProjectsList());
    }

    @ApiOperation(value = "根据镜像仓库获取镜像文件列表", notes = "根据镜像仓库获取镜像文件列表", response = Result.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project", value = "仓库名称", required = true, dataType = "String")
    })
    @GetMapping("/getFileList")
    public Result getFileList(String project) {
        return Result.success(harborService.getKerberosFileList(project));
    }

    @ApiOperation(value = "根据指定的镜像文件获取镜像列表", notes = "根据指定的镜像文件获取镜像列表", response = Result.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project", value = "仓库名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "file", value = "镜像文件", required = true, dataType = "String")
    })
    @GetMapping("/getImageList")
    public Result getImageList(String project, String file) {
        return Result.success(harborService.getKerberosImageList(project, file));
    }

    @ApiOperation(value = "根据指定的镜像img获取镜像信息", notes = "根据指定的镜像img获取镜像信息", response = Result.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project", value = "仓库名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "镜像文件路径", required = true, dataType = "String"),
            @ApiImplicitParam(name = "img", value = "镜像Img", required = true, dataType = "String")
    })
    @GetMapping("/getImageTag")
    public Result getImageTag(String project, String path, String img) {
        return Result.success(harborService.getKerberosImageTag(project, path, img));
    }
}
