package cn.iocoder.teach-ai.module.clientSystem.controller.client.dataobject.dto;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class RepoCategoryDTO {

    /**
     * 知识库类别id
     */
    private Long id;
    /**
     * 知识库类别名称
     */
    private String repoCategoryName;
    /**
     * 知识库内容
     * */
    private List<RepoDTO> repoDTOS;
}
