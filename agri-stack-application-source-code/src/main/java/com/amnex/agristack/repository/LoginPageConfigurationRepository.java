package com.amnex.agristack.repository;

import java.util.Optional;

import com.amnex.agristack.entity.LoginPageConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LoginPageConfigurationRepository extends JpaRepository<LoginPageConfiguration, Long> {

	public Optional<LoginPageConfiguration> findByStateLgdCode(Long stateLgdCode);

	public Optional<LoginPageConfiguration> findByStateLgdCodeAndLandingPageFor(Long stateLgdCode,
			Integer landingPageFor);

}
