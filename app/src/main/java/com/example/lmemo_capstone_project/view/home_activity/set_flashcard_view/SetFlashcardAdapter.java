package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.review_activity.MultipleChoiceTestActivity;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.review_activity.WritingTestActivity;

import java.util.List;

public class SetFlashcardAdapter extends BaseAdapter {
    public static final int OFFLINE_MODE = 0;
    public static final int ONLINE_MODE = 1;
    private static final int WRITING = 1;
    private static final int MULTIPLE_CHOICE = 2;

    private List<SetFlashcard> setFlashcardList;
    private Activity aContext;
    private int mode;
    private User currentUser;

    public SetFlashcardAdapter(List<SetFlashcard> setFlashcardList, Activity aContext, int mode) {
        this.setFlashcardList = setFlashcardList;
        this.aContext = aContext;
        this.mode = mode;
        currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
    }

    @Override
    public int getCount() {
        return setFlashcardList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_set_flashcard_list_adapter, null);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SetFlashcard setFlashcard = setFlashcardList.get(position);

        setupContent(holder, setFlashcard);
        setVisibleForButtons(setFlashcard, holder);
        setActionForButtons(setFlashcard, holder);
        return convertView;
    }

    private void setupContent(ViewHolder holder, SetFlashcard setFlashcard) {
        holder.tvSetName.setText("Set Name: " + setFlashcard.getSetName() + "\nContain: " + setFlashcard.getWordID().size() + " words");
        holder.tvCreatorDisplayName.setText(setFlashcard.getCreator().getDisplayName());
        Log.e("Contribution_Point:", "" + setFlashcard.getCreator().getContributionPoint());
        Reward[] bestReward = LMemoDatabase.getInstance(aContext).rewardDAO().getBestReward(setFlashcard.getCreator().getContributionPoint());
        Reward reward = bestReward[0];
        String rewardName = reward.getRewardName();
        holder.tvCreatorReward.setText(rewardName);
    }

    private void setActionForButtons(final SetFlashcard setFlashcard, final ViewHolder holder) {
        final SetFlashcardController setFlashcardController = new SetFlashcardController(aContext);
        holder.swPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetCheckingController.isOnline(aContext)) {
                    if (holder.swPublic.isChecked()) {
                        try {
                            ProgressDialog.getInstance().show(aContext);
                            setFlashcardController.uploadSetToFirebase(setFlashcard);
                        } catch (UnsupportedOperationException e) {
                            holder.swPublic.setChecked(false);
                            ProgressDialog.getInstance().dismiss();
                            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ProgressDialog.getInstance().show(aContext);
                        setFlashcardController.makeSetPrivate(setFlashcard);
                        if (!isInOfflineMode()) {
                            setFlashcardList.remove(setFlashcard);
                            notifyDataSetChanged();
                        }
                    }
                } else {
                    holder.swPublic.setChecked(!holder.swPublic.isChecked());
                    notifyNoInternet();
                }
            }
        });
//        holder.swPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (InternetCheckingController.isOnline(aContext)) {
//                    if (isChecked) {
//                        try {
//                            ProgressDialog.getInstance().show(aContext);
//                            setFlashcardController.uploadSetToFirebase(setFlashcard);
//                        } catch (UnsupportedOperationException e) {
//                            holder.swPublic.setChecked(false);
//                            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        ProgressDialog.getInstance().show(aContext);
//                        setFlashcardController.makeSetPrivate(setFlashcard);
//                        setFlashcardList.remove(setFlashcard);
//                    }
//                } else {
//                    holder.swPublic.setChecked(false);
//                    notifyNoInternet();
//                }
//                notifyDataSetChanged();
//            }
//        });
        holder.ibChangeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aContext, CreateSetActivity.class);
                intent.putExtra("mode", CreateSetActivity.IN_EDITING_MODE);
                intent.putExtra("set", setFlashcard);
                aContext.startActivity(intent);
            }
        });
        holder.ibReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTestControlDialog(setFlashcard);
            }
        });
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setFlashcard.isPublic() && isOwner(setFlashcard)) {
                    if (InternetCheckingController.isOnline(aContext)) {
                        ProgressDialog.getInstance().show(aContext);
                        setFlashcardController.deleteSetFromFirebase(setFlashcard);
                        setFlashcardList.remove(setFlashcard);
                    } else {
                        notifyNoInternet();
                    }
                } else {
                    setFlashcardController.deleteOfflineSet(setFlashcard);
                    setFlashcardList.remove(setFlashcard);
                }
                notifyDataSetChanged();
            }
        });
        holder.ibDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlashcardController.downloadSet(setFlashcard);
            }
        });
    }

    /**
     * @param setFlashcard テストするセット
     *                     この関数はテストの設定のダイアログを作成します。
     */
    private void createTestControlDialog(final SetFlashcard setFlashcard) {
        final Dialog dialog = new Dialog(aContext);
        dialog.setContentView(R.layout.dialog_test_control);
        dialog.setTitle("Test setting: ");
        dialog.findViewById(R.id.btStartTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNumberOfQuestion = dialog.findViewById(R.id.etNumberOfQuestion);
                if (etNumberOfQuestion.getText().toString().length() != 0) {
                    int numberOfTest = Integer.parseInt(etNumberOfQuestion.getText().toString());
                    if (numberOfTest == 0 || numberOfTest > numberOfFlashcards(setFlashcard)) {
                        etNumberOfQuestion.setError(numberOfTest == 0 ? "This must different than 0" : "Must smaller than " + (numberOfFlashcards(setFlashcard) + 1));
                    } else {
                        int checkedRadioButtonId = ((RadioGroup) dialog.findViewById(R.id.rgTestMode)).getCheckedRadioButtonId();
                        int testMode = ((RadioButton) dialog.findViewById(checkedRadioButtonId)).getText().toString().equals("Writing") ? WRITING : MULTIPLE_CHOICE;
                        Intent intent = new Intent(aContext, WritingTestActivity.class);
                        switch (testMode) {
                            case MULTIPLE_CHOICE:
                                if (numberOfFlashcards(setFlashcard) < 4) {
                                    Toast.makeText(aContext, "There are not enough flashcards to create a multiple-choice test. Required at least 4.", Toast.LENGTH_LONG).show();
                                } else {
                                    intent = new Intent(aContext, MultipleChoiceTestActivity.class);
                                }
                                break;
                        }
                        intent.putExtra(aContext.getString(R.string.number_of_questions), numberOfTest);
                        intent.putExtra(aContext.getString(R.string.set_container), setFlashcard);
                        aContext.startActivityForResult(intent, HomeActivity.TEST_FLASHCARD_REQUEST_CODE);
                        dialog.dismiss();
                    }
                } else {
                    etNumberOfQuestion.setError("This is required");
                }
            }
        });
        dialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Log.i("Dialog", "Created");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private int numberOfFlashcards(SetFlashcard setFlashcard) {
        return setFlashcard.getWordID().size();
    }

    private void notifyNoInternet() {
        Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
    }

    private void setVisibleForButtons(SetFlashcard setFlashcard, ViewHolder holder) {
        boolean isOwner = isOwner(setFlashcard);
        boolean isGuest = isBelongToGuest(setFlashcard);
        boolean isInOfflineMode = isInOfflineMode();
        holder.swPublic.setVisibility((isOwner && !isGuest) ? View.VISIBLE : View.INVISIBLE);
        holder.swPublic.setChecked(setFlashcard.isPublic());
        holder.ibChangeWord.setVisibility(isOwner ? View.VISIBLE : View.INVISIBLE);
        holder.ibDelete.setVisibility((isOwner || isInOfflineMode) ? View.VISIBLE : View.INVISIBLE);
        holder.ibReview.setVisibility((isInOfflineMode && (numberOfFlashcards(setFlashcard) != 0)) ? View.VISIBLE : View.INVISIBLE);
        holder.ibDownload.setVisibility((!isInOfflineMode && !isOwner(setFlashcard)) ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isBelongToGuest(SetFlashcard setFlashcard) {
        return setFlashcard.getCreatorID().equalsIgnoreCase("GUEST");
    }

    private boolean isInOfflineMode() {
        return mode == OFFLINE_MODE;
    }

    private boolean isOwner(SetFlashcard setFlashcard) {
        return setFlashcard.getCreatorID().equalsIgnoreCase(currentUser.getUserID());
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvSetName = convertView.findViewById(R.id.tvSetName);
        viewHolder.swPublic = convertView.findViewById(R.id.swPublic);
        viewHolder.ibReview = convertView.findViewById(R.id.ibReview);
        viewHolder.ibChangeWord = convertView.findViewById(R.id.ibChangeWord);
        viewHolder.ibDelete = convertView.findViewById(R.id.ibDelete);
        viewHolder.ibDownload = convertView.findViewById(R.id.ibDownload);
        viewHolder.tvCreatorDisplayName = convertView.findViewById(R.id.tvCreatorDisplayName);
        viewHolder.tvCreatorReward = convertView.findViewById(R.id.tvCreatorReward);

        return viewHolder;
    }

    private static class ViewHolder {
        TextView tvSetName, tvCreatorDisplayName, tvCreatorReward;
        Switch swPublic;
        ImageButton ibReview, ibChangeWord, ibDelete, ibDownload;
    }
}
