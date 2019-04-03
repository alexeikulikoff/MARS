package com.mibs.mars.service;

import com.mibs.mars.utils.MUtils;

public interface ExplorationNameProvider {
	default String ExplorationName() {
		return MUtils.UniqueID();
	}
}
