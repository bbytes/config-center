package com.bbytes.ccenter.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.ccenter.domain.User;
import com.bbytes.ccenter.repository.UserRepository;

@Service
public class SecretKeyServiceImpl implements SecretKeyService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public String getSecretKey(String clientId) throws SecretKeyNotFoundException {
    User user = userRepository.findByClientId(clientId);
    if (user != null)
      return user.getSecretKey();

    throw new SecretKeyNotFoundException("Client id : " + clientId);

  }

  @Override
  public String getDefaultSecretKey() throws SecretKeyNotFoundException {
    throw new SecretKeyNotFoundException("GetDefaultSecretKey method not to be used");
  }

  @Override
  public boolean useDefault() {
    return false;
  }

}
