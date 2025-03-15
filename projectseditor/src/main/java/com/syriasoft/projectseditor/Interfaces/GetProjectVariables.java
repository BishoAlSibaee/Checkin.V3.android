package com.syriasoft.projectseditor.Interfaces;

import com.syriasoft.projectseditor.Classes.PROJECT_VARIABLES;

public interface GetProjectVariables {
    void onSuccess(PROJECT_VARIABLES variables);
    void onError(String error);
}
