package com.rag.knowbase.viewmodel;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.rag.knowbase.model.UserCollection;

import java.util.ArrayList;
import java.util.List;

public class CollectionViewModel extends ViewModel {


    public String dialogName        = "";
    public String dialogDescription = "";
    public String dialogChatName    = "";
    public boolean isFilePicking    = false;
    public List<Uri> selectedFileUris = new ArrayList<>();


    public List<UserCollection> collections    = new ArrayList<>();
    public List<UserCollection> allCollections = new ArrayList<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        selectedFileUris.clear();
        dialogName = "";
        dialogDescription = "";
        dialogChatName = "";
    }

}
