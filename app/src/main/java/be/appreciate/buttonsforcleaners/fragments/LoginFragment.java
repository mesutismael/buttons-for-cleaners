package be.appreciate.buttonsforcleaners.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.activities.MainActivity;
import be.appreciate.buttonsforcleaners.api.ApiHelper;
import be.appreciate.buttonsforcleaners.utils.DialogHelper;
import be.appreciate.buttonsforcleaners.utils.Observer;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class LoginFragment extends Fragment implements View.OnClickListener
{
    private EditText editTextDomain;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private MaterialDialog dialogProgress;

    public static LoginFragment newInstance()
    {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        this.editTextDomain = (EditText) view.findViewById(R.id.editText_domain);
        this.editTextUsername = (EditText) view.findViewById(R.id.editText_username);
        this.editTextPassword = (EditText) view.findViewById(R.id.editText_password);
        Button buttonLogIn = (Button) view.findViewById(R.id.button_logIn);

        String domain = PreferencesHelper.getDomain(view.getContext());
        this.editTextDomain.setText(domain);

        buttonLogIn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_logIn:
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String domain = this.editTextDomain.getText().toString();
                String username = this.editTextUsername.getText().toString();
                String password = this.editTextPassword.getText().toString();

                if(TextUtils.isEmpty(domain) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
                {
                    this.showIncompleteDialog(v.getContext());
                }
                else
                {
                    PreferencesHelper.saveDomain(v.getContext(), domain);
                    this.showProgressDialog(v.getContext());
                    ApiHelper.logIn(v.getContext(), username, password).subscribe(this.loginObserver);
                }
                break;
        }
    }

    private void showIncompleteDialog(Context context)
    {
        new MaterialDialog.Builder(context)
                .title(R.string.dialog_error)
                .content(R.string.login_incomplete_error)
                .positiveText(R.string.dialog_positive)
                .show();
    }

    private void showLoginErrorDialog(Context context)
    {
        new MaterialDialog.Builder(context)
                .title(R.string.dialog_error)
                .content(R.string.login_login_error)
                .positiveText(R.string.dialog_positive)
                .show();
    }

    private void showApiErrorDialog(Context context)
    {
        new MaterialDialog.Builder(context)
                .title(R.string.dialog_error)
                .content(R.string.login_api_error)
                .positiveText(R.string.dialog_positive)
                .show();
    }

    private void showProgressDialog(Context context)
    {
        this.dialogProgress = new MaterialDialog.Builder(context)
                .content(R.string.login_progress)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    private void startNextActivity()
    {
        if(this.getContext() != null)
        {
            Intent intent = MainActivity.getIntent(this.getContext());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
    }

    private Observer<Object> loginObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            if(LoginFragment.this.getContext() != null)
            {
                ApiHelper.doStartUpCalls(LoginFragment.this.getContext()).subscribe(LoginFragment.this.startUpObserver);
            }
        }

        @Override
        public void onError(Throwable e)
        {
            if(LoginFragment.this.dialogProgress != null)
            {
                LoginFragment.this.dialogProgress.dismiss();
            }

            if(LoginFragment.this.getContext() != null && DialogHelper.canShowDialog(LoginFragment.this))
            {
                LoginFragment.this.showLoginErrorDialog(LoginFragment.this.getContext());
            }
        }
    };

    private Observer<Object> startUpObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            if(LoginFragment.this.dialogProgress != null)
            {
                LoginFragment.this.dialogProgress.dismiss();
            }

            LoginFragment.this.startNextActivity();
        }

        @Override
        public void onError(Throwable e)
        {
            if(LoginFragment.this.dialogProgress != null)
            {
                LoginFragment.this.dialogProgress.dismiss();
            }

            if(LoginFragment.this.getContext() != null && DialogHelper.canShowDialog(LoginFragment.this))
            {
                LoginFragment.this.showApiErrorDialog(LoginFragment.this.getContext());
            }
        }
    };
}
