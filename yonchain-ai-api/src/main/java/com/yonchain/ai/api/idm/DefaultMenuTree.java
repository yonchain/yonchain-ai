package com.yonchain.ai.api.idm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 默认菜单树实现
 */
/**
 * 默认菜单树实现
 */
public class DefaultMenuTree extends DefaultMenu implements MenuTree<DefaultMenuTree> {
    
    private List<DefaultMenuTree> children;

    @Override
    public List<DefaultMenuTree> getChildren() {
        return Optional.ofNullable(children).orElse(new ArrayList<>());
    }

    @Override
    public void setChildren(List<DefaultMenuTree> children) {
        this.children = children;
    }
}
