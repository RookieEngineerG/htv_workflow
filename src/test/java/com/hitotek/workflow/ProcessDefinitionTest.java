package com.hitotek.workflow;

import com.hitotek.workflow.model.Data;
import com.hitotek.workflow.model.multipart.MultiPartDataManager;
import com.hitotek.workflow.model.multipart.MultipartData;
import com.hitotek.workflow.service.ProcessDefinitionService;
import com.sun.org.apache.xpath.internal.operations.Mult;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: ztrue
 * @date: 2019-07-10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ProcessDefinitionTest {
    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @Test
    public void testDeleteAllDefinitionProcess () {
        Data processDefinitions = processDefinitionService.getProcessDefinitions(null);

        List<MultipartData> data = processDefinitions.getListPart("data", MultipartData.class);
        for (MultipartData multipartData : data) {
            String deploymentId = multipartData.getStringPart("deploymentId");
            processDefinitionService.removeProcessDefinitions(deploymentId);
            log.info("删除部署流程{}成功", deploymentId);
        }

    }
}
