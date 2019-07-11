package com.hitotek.workflow.service.impl;

import com.hitotek.workflow.factory.DataFactory;
import com.hitotek.workflow.model.Data;
import com.hitotek.workflow.model.multipart.MultipartData;
import com.hitotek.workflow.service.ProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: ztrue
 * @date: 2019-07-10
 */
@Service
@Slf4j
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 删除流程实例  【根据流程实例删除当前流程，不可逆操作】
     *
     * @param instanceId 流程实例id
     */
    @Override
    public Data deleteProcessInstance(String instanceId) {
        try {
            // 第二个参数: 删除原因
            runtimeService.deleteProcessInstance(instanceId, "");

            return DataFactory.createSuccess().message("流程实例删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataFactory.createError().message("流程实例删除失败!");
        }
    }

    /**
     * 启动流程
     * @param definitionKey 流程部署key
     * @param variables 参数
     */
    @Override
    public Data startProcessInstanceByKey(String definitionKey, MultipartData variables) {
        try {
            //启动流程
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceByKey(definitionKey, variables);

            log.info("启动流程[{}]成功！", definitionKey);
            return DataFactory.createSuccess()
                    .data(new MultipartData().parties(processInstance));
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataFactory.createError().message("启动流程失败！");
        }
    }
}
