package com.bbytes.ccenter.repository;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import com.bbytes.ccenter.domain.User;
import com.mongodb.DBObject;

/**
 * Class called when we query or save the user object into mongo db
 * @author Thanneer
 *
 */
public class UserEventListener extends AbstractMongoEventListener<User> {

	@Override
	public void onBeforeConvert(User user) {

	}

	@Override
	public void onBeforeSave(User user, DBObject dbo) {
		user.setClientId(RepositoryUtils.generateSmallRandomAlphabetic());
		user.setSecretKey(RepositoryUtils.generateBigRandomAlphabetic());
	}

	@Override
	public void onAfterSave(User user, DBObject dbo) {

	}

	@Override
	public void onAfterLoad(DBObject dbo) {

	}

	@Override
	public void onAfterConvert(DBObject dbo, User user) {

	}

}