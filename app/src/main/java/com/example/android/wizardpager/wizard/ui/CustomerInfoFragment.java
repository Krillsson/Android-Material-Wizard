/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wizardpager.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.wizardpager.R;
import com.example.android.wizardpager.wizard.WizardActivity;
import com.example.android.wizardpager.wizard.model.CustomerInfoPage;

public class CustomerInfoFragment extends Fragment implements WizardActivity.MiddleActionButtonClick {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private CustomerInfoPage mPage;
    private TextView mEmailView;
    private TextView mPasswordView;
    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;

    private View rootView;

    public static CustomerInfoFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        CustomerInfoFragment fragment = new CustomerInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CustomerInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (CustomerInfoPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_page_customer_info, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        mEmailView = ((TextView) rootView.findViewById(R.id.email));
        mEmailView.setText(mPage.getData().getString(CustomerInfoPage.EMAIL_DATA_KEY));
        mEmailInputLayout = (TextInputLayout) rootView.findViewById(R.id.text_input_layout_email);

        mPasswordView = ((TextView) rootView.findViewById(R.id.password));
        mPasswordView.setText(mPage.getData().getString(CustomerInfoPage.PASSWORD_DATA_KEY));
        mPasswordInputLayout = (TextInputLayout) rootView.findViewById(R.id.text_input_layout_password);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && v != null) {
                    EditText editText = (EditText) v;
                    Editable editable = editText.getText();
                    if (editable != null) {
                        if (TextUtils.isEmpty(editable.toString())) {
                            mEmailInputLayout.setError("Email field can't be empty");
                        }
                    }
                }
            }
        });

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(CustomerInfoPage.EMAIL_DATA_KEY,
                        (editable != null) ? editable.toString() : null);
            }
        });

        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && v != null) {
                    EditText editText = (EditText) v;
                    Editable editable = editText.getText();
                    if (editable != null) {
                        if (TextUtils.isEmpty(editable.toString())) {
                            mPasswordInputLayout.setError("Password field can't be empty");
                        }
                    }
                }
            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(CustomerInfoPage.PASSWORD_DATA_KEY,
                        (editable != null) ? editable.toString() : null);
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    assertValidCredentials();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mEmailView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private boolean isValidEmail(String text) {
        return text != null && text.length() > 0 && (Patterns.EMAIL_ADDRESS.matcher(text).matches());
    }

    public void assertValidCredentials() {
        if (!TextUtils.isEmpty(mEmailView.getText().toString()) && !TextUtils.isEmpty(mPasswordView.getText().toString())) {
            mEmailView.setError(null);
            mPasswordView.setError(null);
            mPage.notifyDataChanged();
        } else {
            if (TextUtils.isEmpty(mEmailView.getText().toString())) {
                mEmailInputLayout.setError("Username can't be empty");
            }
            if (TextUtils.isEmpty(mPasswordView.getText().toString())) {
                mPasswordInputLayout.setError("Password can't be empty");
            }
        }


    }

    @Override
    public void onMiddleActionButtonClick() {
        assertValidCredentials();
    }
}
