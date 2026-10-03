package com.kalaconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.kalaconnect.R;
import com.kalaconnect.utils.LocaleHelper;

public class LanguageSelectionDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "LanguageSelectionDialog";

    private RadioGroup rgLanguageSelector;
    private MaterialRadioButton rbEnglish;
    private MaterialRadioButton rbHindi;
    private MaterialRadioButton rbMarathi;
    private MaterialButton btnCancel;
    private MaterialButton btnApply;

    public static LanguageSelectionDialogFragment newInstance() {
        return new LanguageSelectionDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_language, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rgLanguageSelector = view.findViewById(R.id.rgLanguageSelector);
        rbEnglish = view.findViewById(R.id.rbLanguageEnglish);
        rbHindi = view.findViewById(R.id.rbLanguageHindi);
        rbMarathi = view.findViewById(R.id.rbLanguageMarathi);
        btnCancel = view.findViewById(R.id.btnCancelLanguage);
        btnApply = view.findViewById(R.id.btnApplyLanguage);

        String currentLanguage = LocaleHelper.getLanguage(requireContext());
        if (LocaleHelper.LANGUAGE_HINDI.equals(currentLanguage)) {
            rbHindi.setChecked(true);
        } else if (LocaleHelper.LANGUAGE_MARATHI.equals(currentLanguage)) {
            rbMarathi.setChecked(true);
        } else {
            rbEnglish.setChecked(true);
        }

        btnCancel.setOnClickListener(v -> dismiss());

        btnApply.setOnClickListener(v -> {
            String selectedLang = LocaleHelper.LANGUAGE_ENGLISH;
            int checkedId = rgLanguageSelector.getCheckedRadioButtonId();

            if (checkedId == R.id.rbLanguageHindi) {
                selectedLang = LocaleHelper.LANGUAGE_HINDI;
            } else if (checkedId == R.id.rbLanguageMarathi) {
                selectedLang = LocaleHelper.LANGUAGE_MARATHI;
            }

            LocaleHelper.setLocale(requireContext(), selectedLang);
            Toast.makeText(requireContext(), R.string.language_changed_toast, Toast.LENGTH_SHORT).show();
            dismiss();

            if (getActivity() != null) {
                getActivity().recreate();
            }
        });
    }
}
