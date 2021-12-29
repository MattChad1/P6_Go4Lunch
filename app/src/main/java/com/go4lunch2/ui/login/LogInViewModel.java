package com.go4lunch2.ui.login;

import androidx.lifecycle.ViewModel;

import com.go4lunch2.data.repositories.UserRepository;

public class LogInViewModel extends ViewModel {

    private UserRepository userRepository;

    public LogInViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(String idFirebase, String name, String avatar) {
        userRepository.createUser(idFirebase, name, avatar);
    }
}
