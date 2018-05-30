package com.kustomer.kustomersdk.Activities;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.MessageListAdapter;
import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSTeamsDataSource;
import com.kustomer.kustomersdk.Enums.KUSChatMessageType;
import com.kustomer.kustomersdk.Enums.KUSFormQuestionProperty;
import com.kustomer.kustomersdk.Helpers.KUSPermission;
import com.kustomer.kustomersdk.Helpers.KUSText;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSEmailInputViewListener;
import com.kustomer.kustomersdk.Interfaces.KUSInputBarViewListener;
import com.kustomer.kustomersdk.Interfaces.KUSOptionPickerViewListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSFormQuestion;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSTeam;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Utils.KUSUtils;
import com.kustomer.kustomersdk.Views.KUSEmailInputView;
import com.kustomer.kustomersdk.Views.KUSInputBarView;
import com.kustomer.kustomersdk.Views.KUSLargeImageViewer;
import com.kustomer.kustomersdk.Views.KUSOptionsPickerView;
import com.kustomer.kustomersdk.Views.KUSToolbar;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class KUSChatActivity extends BaseActivity implements KUSChatMessagesDataSourceListener, KUSToolbar.OnToolbarItemClickListener, KUSEmailInputViewListener, KUSInputBarViewListener, KUSOptionPickerViewListener, MessageListAdapter.ChatMessageItemListener {

    //region Properties
    private static final int REQUEST_IMAGE_CAPTURE = 1122;
    private static final int GALLERY_INTENT = 1123;
    private static final int REQUEST_CAMERA_PERMISSION = 1133;
    private static final int REQUEST_STORAGE_PERMISSION = 1134;

    @BindView(R2.id.rvMessages)
    RecyclerView rvMessages;
    @BindView(R2.id.emailInputView)
    KUSEmailInputView emailInputView;
    @BindView(R2.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R2.id.kusInputBarView)
    KUSInputBarView kusInputBarView;
    @BindView(R2.id.kusOptionPickerView)
    KUSOptionsPickerView kusOptionPickerView;

    KUSChatSession kusChatSession;
    KUSUserSession userSession;
    KUSChatMessagesDataSource chatMessagesDataSource;
    KUSTeamsDataSource teamOptionsDatasource;
    String chatSessionId;
    MessageListAdapter adapter;
    KUSToolbar kusToolbar;
    boolean shouldShowBackButton = true;
    boolean backPressed = false;

    String mCurrentPhotoPath;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_kuschat, R.id.toolbar_main, null, false);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initData();
        initViews();
        setupAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        kusInputBarView.requestInputFocus();

        if (userSession != null && chatSessionId != null)
            userSession.getChatSessionsDataSource().updateLastSeenAtForSessionId(chatSessionId, null);
    }


    @Override
    protected void onPause() {
        if (userSession != null && chatSessionId != null)
            userSession.getChatSessionsDataSource().updateLastSeenAtForSessionId(chatSessionId, null);

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (chatMessagesDataSource != null)
            chatMessagesDataSource.removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (shouldShowBackButton) {
            backPressed = true;
            super.onBackPressed();
        } else {
            clearAllLibraryActivities();
        }
    }

    @Override
    public void finish() {
        super.finish();

        if (backPressed)
            overridePendingTransition(R.anim.stay, R.anim.kus_slide_right);
        else
            overridePendingTransition(R.anim.stay, R.anim.kus_slide_down);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        checkShouldShowEmailInput();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK){
                String photoUri = KUSUtils.getUriFromFile(this, new File(mCurrentPhotoPath)).toString();

                kusInputBarView.attachImage(photoUri);
                mCurrentPhotoPath = null;
            }else{
                mCurrentPhotoPath = null;
            }
        }else if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            if (data != null) {
                String photoUri = data.getDataString();
                kusInputBarView.attachImage(photoUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, R.string.camera_permission_denied,Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_STORAGE_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, R.string.storage_permission_denied,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //endregion

    //region Initializer
    private void initData() {
        userSession = Kustomer.getSharedInstance().getUserSession();
        kusChatSession = (KUSChatSession) getIntent().getSerializableExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE_KEY);
        shouldShowBackButton = getIntent().getBooleanExtra(KUSConstants.BundleName.CHAT_SCREEN_BACK_BUTTON_KEY, true);

        if (kusChatSession != null) {
            chatSessionId = kusChatSession.getId();
            chatMessagesDataSource = userSession.chatMessageDataSourceForSessionId(chatSessionId);
        } else {
            chatMessagesDataSource = new KUSChatMessagesDataSource(userSession, true);
        }

        chatMessagesDataSource.addListener(this);
        chatMessagesDataSource.fetchLatest();
        if (!chatMessagesDataSource.isFetched()) {
            progressDialog.show();
        }
    }

    private void initViews() {
        kusInputBarView.setListener(this);
        kusInputBarView.setAllowsAttachment(chatSessionId != null);
        kusOptionPickerView.setListener(this);
        setupToolbar();
    }

    private void setupToolbar() {
        kusToolbar = (KUSToolbar) toolbar;
        kusToolbar.initWithUserSession(userSession);
        kusToolbar.setExtraLargeSize(chatMessagesDataSource.getSize() == 0);
        kusToolbar.setSessionId(chatSessionId);
        kusToolbar.setShowLabel(true);
        kusToolbar.setListener(this);
        kusToolbar.setShowBackButton(shouldShowBackButton);
        kusToolbar.setShowDismissButton(true);

        checkShouldShowEmailInput();
    }

    private void checkShouldShowEmailInput() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (isLandscape && KUSUtils.isPhone(this)) {
            appBarLayout.setLayoutTransition(null);

            emailInputView.setVisibility(View.GONE);
        } else {
            boolean shouldShowEmailInput = userSession.isShouldCaptureEmail() && chatSessionId != null;
            appBarLayout.setLayoutTransition(new LayoutTransition());

            if (shouldShowEmailInput) {
                emailInputView.setVisibility(View.VISIBLE);
                emailInputView.setListener(this);
            } else {
                emailInputView.setVisibility(View.GONE);
            }
        }
    }

    private void checkShouldShowOptionPicker() {
        KUSFormQuestion currentQuestion = chatMessagesDataSource.currentQuestion();
        boolean wantsOptionPicker = (currentQuestion != null
                && currentQuestion.getProperty() == KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CONVERSATION_TEAM
                && currentQuestion.getValues().size() > 0);

        boolean teamOptionsDidFail = teamOptionsDatasource != null && (teamOptionsDatasource.getError() != null
                || (teamOptionsDatasource.isFetched() && teamOptionsDatasource.getSize() == 0));
        if (wantsOptionPicker && !teamOptionsDidFail) {
            kusInputBarView.setVisibility(View.GONE);
            kusInputBarView.clearInputFocus();

            List<String> teamIds = currentQuestion.getValues();
            if (teamOptionsDatasource == null || !teamOptionsDatasource.getTeamIds().equals(teamIds)) {
                teamOptionsDatasource = new KUSTeamsDataSource(userSession, teamIds);
                teamOptionsDatasource.addListener(this);
                teamOptionsDatasource.fetchLatest();
            }

            kusOptionPickerView.setVisibility(View.VISIBLE);

        } else {
            teamOptionsDatasource = null;

            kusInputBarView.setVisibility(View.VISIBLE);
            kusOptionPickerView.setVisibility(View.GONE);
        }
    }

    private void updateOptionsPickerOptions() {
        List<String> options = new ArrayList<>();

        for(KUSModel model : teamOptionsDatasource.getList()){
            KUSTeam team = (KUSTeam) model;
            options.add(team.fullDisplay());
        }

        kusOptionPickerView.setOptions(options);
    }

    private void setupAdapter() {
        adapter = new MessageListAdapter(chatMessagesDataSource, userSession, chatMessagesDataSource, this);
        rvMessages.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvMessages.setLayoutManager(layoutManager);

        adapter.notifyDataSetChanged();
    }

    private void openCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!KUSPermission.isCameraPermissionAvailable(this)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);

            } else {
                dispatchTakePictureIntent();
            }
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {
            }

            if (photoFile != null) {
                Uri photoURI = KUSUtils.getUriFromFile(this, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!KUSPermission.isStoragePermissionAvailable(this)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);

            } else {
                startGalleryIntent();
            }
        } else {
            startGalleryIntent();
        }
    }



    private void startGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
    }
    //endregion

    //region Listeners
    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.hide();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {
        if (dataSource == chatMessagesDataSource && !chatMessagesDataSource.isFetched()) {
            final WeakReference<KUSChatActivity> weakReference = new WeakReference<>(this);
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    KUSChatActivity strongReference = weakReference.get();
                    if (strongReference != null)
                        strongReference.chatMessagesDataSource.fetchLatest();
                }
            };
            handler.postDelayed(runnable, 1000);
        } else if (dataSource == teamOptionsDatasource) {
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    checkShouldShowOptionPicker();
                }
            };
            handler.post(runnable);
        }
    }

    @Override
    public void onContentChange(final KUSPaginatedDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dataSource == chatMessagesDataSource) {
                    checkShouldShowOptionPicker();

                    if (dataSource.getSize() > 1)
                        setupToolbar();

                    adapter.notifyDataSetChanged();
                } else if (dataSource == teamOptionsDatasource) {
                    checkShouldShowOptionPicker();
                    updateOptionsPickerOptions();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onCreateSessionId(final KUSChatMessagesDataSource source, final String sessionId) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                chatSessionId = sessionId;
                kusInputBarView.setAllowsAttachment(true);
                kusToolbar.setSessionId(chatSessionId);
                shouldShowBackButton = true;
                kusToolbar.setShowBackButton(true);
                setupToolbar();
                checkShouldShowEmailInput();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onToolbarBackPressed() {
        onBackPressed();
    }

    @Override
    public void onToolbarClosePressed() {
        clearAllLibraryActivities();
    }


    @Override
    public void onSubmitEmail(String email) {
        userSession.submitEmail(email);
        checkShouldShowEmailInput();
    }

    @Override
    public void inputBarAttachmentClicked() {
        ArrayList<String> itemsList = new ArrayList<>();
        String[] items = null;

        if(KUSPermission.isCameraPermissionDeclared(this))
            itemsList.add(getString(R.string.camera));
        if(KUSPermission.isReadPermissionDeclared(this))
            itemsList.add(getString(R.string.gallery));

        if(itemsList.size() > 0) {
            items = new String[itemsList.size()];
            items = itemsList.toArray(items);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // camera
                        openCamera();
                    break;

                    case 1: // gallery
                        openGallery();
                    break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void inputBarSendClicked() {
        if (chatMessagesDataSource.shouldPreventSendingMessage())
            return;

        final String text = kusInputBarView.getText();

        if(!text.isEmpty()) {
            //Sending Data in background
            new Thread(new Runnable() {
                @Override
                public void run() {
                    chatMessagesDataSource.sendMessageWithText(text, kusInputBarView.getAllImages());
                }
            }).start();


            kusInputBarView.setText("");
            kusInputBarView.removeAllAttachments();
        }
    }

    @Override
    public boolean inputBarShouldEnableSend() {
        KUSFormQuestion currentQuestion = chatMessagesDataSource.currentQuestion();
        if (currentQuestion != null && currentQuestion.getProperty() == KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CUSTOMER_EMAIL)
            return KUSText.isValidEmail(kusInputBarView.getText());

        return kusInputBarView.getText().length() > 0;
    }

    @Override
    public void optionPickerOnOptionSelected(String option) {
        String value = null;
        KUSTeam team = null;

        int optionIndex = kusOptionPickerView.getOptions().indexOf(option);
        KUSFormQuestion currentQuestion = chatMessagesDataSource.currentQuestion();
        if (optionIndex >= 0 && optionIndex < (currentQuestion != null ? currentQuestion.getValues().size() : 0)) {
            value = currentQuestion.getValues().get(optionIndex);
        }
        if (optionIndex >= 0 && optionIndex < (teamOptionsDatasource != null ? teamOptionsDatasource.getSize() : 0))
            team = (KUSTeam) teamOptionsDatasource.get(optionIndex);

        chatMessagesDataSource.sendMessageWithText(
                team != null && team.displayName != null ? team.displayName : option,
                null,
                value != null ? value : team != null ? team.getId() : null);
    }

    @Override
    public void onChatMessageImageClicked(KUSChatMessage chatMessage) {
        int startingIndex = 0;

        List<String> imageURIs = new ArrayList<>();

        for(int i = chatMessagesDataSource.getSize() -1 ; i >= 0; i--){
            KUSChatMessage kusChatMessage = (KUSChatMessage) chatMessagesDataSource.get(i);
            if(kusChatMessage.getType() == KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_IMAGE){
                imageURIs.add(kusChatMessage.getImageUrl().toString());
            }
        }

        startingIndex = imageURIs.indexOf(chatMessage.getImageUrl().toString());

        new KUSLargeImageViewer(this).showImages(imageURIs,startingIndex);
    }

    @Override
    public void onChatMessageErrorClicked(KUSChatMessage chatMessage) {
        chatMessagesDataSource.resendMessage(chatMessage);
    }
    //endregion
}
