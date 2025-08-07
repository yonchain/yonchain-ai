package com.yonchain.ai.idm.service;

import com.yonchain.ai.api.common.Page;
import com.yonchain.ai.api.idm.OAuth2ClientService;
import com.yonchain.ai.api.idm.OAuth2RegisteredClient;
import com.yonchain.ai.idm.mapper.OAuth2RegisteredClientMapper;
import com.yonchain.ai.utils.PageUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * OAuth2注册客户端服务实现
 *
 * @author Cgy
 * @since 1.0.0
 */
@Service
public class OAuth2ClientServiceImpl implements OAuth2ClientService {

    @Autowired
    private OAuth2RegisteredClientMapper oauth2RegisteredClientMapper;

    @Override
    public OAuth2RegisteredClient getById(String id) {
        return oauth2RegisteredClientMapper.selectById(id);
    }

    @Override
    public OAuth2RegisteredClient getByClientId(String clientId) {
        return oauth2RegisteredClientMapper.selectByClientId(clientId);
    }

    @Override
    public Page<OAuth2RegisteredClient> pageOAuth2RegisteredClients(Map<String, Object> queryParams, int pageNum, int pageSize) {
        // 计算分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询总数
        List<OAuth2RegisteredClient> clients = oauth2RegisteredClientMapper.selectList(queryParams);

        // 返回分页结果
        return PageUtil.convert(clients);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOAuth2RegisteredClient(OAuth2RegisteredClient client) {
        client.setClientIdIssuedAt(new Date());
        client.setClientSecretExpiresAt(Date.from(LocalDate.now().plusYears(100).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        oauth2RegisteredClientMapper.insert(client);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOAuth2RegisteredClient(OAuth2RegisteredClient client) {
        oauth2RegisteredClientMapper.update(client);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOAuth2RegisteredClientById(String id) {
        oauth2RegisteredClientMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOAuth2RegisteredClientByIds(List<String> ids) {
        oauth2RegisteredClientMapper.deleteBatchIds(ids);
    }
}
