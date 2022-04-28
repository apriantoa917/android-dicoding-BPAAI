package com.aprianto.dicostory.ui.auth

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.viewmodel.AuthViewModel
import com.aprianto.dicostory.data.viewmodel.SettingViewModel
import com.aprianto.dicostory.data.viewmodel.ViewModelSettingFactory
import com.aprianto.dicostory.databinding.FragmentLoginBinding
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore


class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SettingPreferences.getInstance((activity as AuthActivity).dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelSettingFactory(pref))[SettingViewModel::class.java]

        viewModel.let { vm ->
            vm.loginResult.observe(viewLifecycleOwner) { login ->
                // success login process triggered -> save preferences
                settingViewModel.setUserPreferences(
                    login.loginResult.token,
                    login.loginResult.userId,
                    login.loginResult.name,
                    viewModel.tempEmail.value ?: Constanta.preferenceDefaultValue
                )
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                error?.let {
                    if (it.isNotEmpty()) {
                        Helper.showDialogInfo(requireContext(), it)
                    }
                }
            }
            vm.loading.observe(viewLifecycleOwner) { state ->
                binding.loading.root.visibility = state
            }
        }
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) { token ->
                // if token triggered change -> redirect to Main Activity
                if (token != Constanta.preferenceDefaultValue) (activity as AuthActivity).routeToMainActivity()
            }
        binding.btnAction.setOnClickListener {
            /*
            *  NOTE REVIWER LALU :
            *  - untuk pengecekan logic tidak dilakukan di sini namun di file custom view
            *  - pengecekan disini -> jika input kosong tampilkan error field kosong
            *  - selain pengecekan field kosong -> tampilkan logic error dari custom view
            * */

            /* check if input is empty or not */
            if (binding.edEmail.text?.length ?: 0 <= 0) {
                binding.edEmail.error = getString(R.string.UI_validation_empty_email)
                binding.edEmail.requestFocus()
            } else if (binding.edPassword.text?.length ?: 0 <= 0) {
                binding.edPassword.error = getString(R.string.UI_validation_empty_password)
                binding.edPassword.requestFocus()
            }
            /* input not empty -> check contains error */
            else if (binding.edEmail.error?.length ?: 0 > 0) {
                binding.edEmail.requestFocus()
            } else if (binding.edPassword.error?.length ?: 0 > 0) {
                binding.edPassword.requestFocus()
            }
            /* not contain error */
            else {
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                viewModel.login(email, password)
            }
        }
        binding.btnRegister.setOnClickListener {
            /* while view models contains error -> clear error before replace fragments (to hide dialog error)*/
            viewModel.error.postValue("")

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                /* shared element transition to main activity */
                addSharedElement(binding.labelAuth, "auth")
                addSharedElement(binding.edEmail, "email")
                addSharedElement(binding.edPassword, "password")
                addSharedElement(binding.containerMisc, "misc")
                commit()
            }
        }
    }

}