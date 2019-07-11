package com.hitotek.workflow.service.impl;

import com.hitotek.workflow.factory.DataFactory;
import com.hitotek.workflow.model.Data;
import com.hitotek.workflow.model.multipart.MultipartData;
import com.hitotek.workflow.service.ProcessDefinitionService;
import com.hitotek.workflow.util.Strings;
import com.hitotek.workflow.util.Values;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ztrue
 * @date: 2019-07-09
 */
@Service
@Slf4j
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 我的流程部署流程
     */
    @Override
    public Data deployMyProcess() {
        try {
            repositoryService.createDeployment()
                    //创建一个部署对象
                    .addClasspathResource("processes/my-process.bpmn20.xml")
                    //添加名称
                    .name("部署流程")
                    //完成部署
                    .deploy();
            return DataFactory.createSuccess().message("我的流程，部署成功! ");
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataFactory.createError().message("我的流程，部署失败! ");
        }
    }

    /**
     * 删除流程
     * @param deploymentId 流程部署id
     */
    @Override
    public Data removeProcessDefinitions(String deploymentId) {
        try {
            if (Strings.isNullOrEmpty(deploymentId)) {
                throw new RuntimeException("流程部署id不能为空");
            }

            repositoryService.deleteDeployment(deploymentId, true);

            return DataFactory.createSuccess().message("递归删除流程部署成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataFactory.createError().message("递归删除流程部署失败");
        }
    }


    /**
     * 获取已部署流程
     * @param deploymentId 流程部署id
     */
    @Override
    public Data getProcessDefinitions(String deploymentId) {
        try {
            List<ProcessDefinition> list;
            if (Strings.isNullOrEmpty(deploymentId)) {
                // 没有流程部署id, 则查询全部
                list = repositoryService.createProcessDefinitionQuery()
                        .orderByProcessDefinitionVersion()
                        .desc()
                        .list();
            } else {
                // 有流程部署id
                list = repositoryService.createProcessDefinitionQuery()
                        .deploymentId(deploymentId)
                        .orderByProcessDefinitionVersion()
                        .desc()
                        .list();
            }
            if (Values.isNull(list) || list.size() == 0) {
                throw new RuntimeException("未获取到流程定义");
            }
            log.info("list.size() = {}", list.size());
            List<MultipartData> data = new ArrayList<>();
            for (ProcessDefinition processDefinition : list) {
                data.add(new MultipartData().parties(processDefinition));
            }
            return DataFactory.createSuccess().data(data);
        } catch (RuntimeException run) {
            log.error(run.getMessage());
            return DataFactory.createFail().message(run.getMessage());
        }

    }

    /**
     * 获取最新部署的流程
     * @param definitionKey 流程部署key
     */
    @Override
    public Data getLatestProcessDefinitions(String definitionKey) {
        if (Strings.isNullOrEmpty(definitionKey)) {
            return DataFactory.createError().message("未获取到流程定义");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(definitionKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .singleResult();
        if (!Values.isNull(processDefinition)) {
            return DataFactory.createSuccess().data(processDefinition);
        } else {
            return DataFactory.createFail();
        }
    }

}
