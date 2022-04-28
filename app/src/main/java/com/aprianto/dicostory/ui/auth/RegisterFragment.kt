package com.aprianto.dicostory.ui.auth

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.viewmodel.AuthViewModel
import com.aprianto.dicostory.databinding.FragmentRegisterBinding
import com.aprianto.dicostory.utils.Helper

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var binding: FragmentRegisterBinding

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { vm ->
            vm.registerResult.observe(viewLifecycleOwner) { register ->
                if (!register.error) {
                    val dialog = Helper.dialogInfoBuilder(
                        (activity as AuthActivity),
                        getString(R.string.UI_info_successful_register_user)
                    )
                    val btnOk = dialog.findViewById<Button>(R.id.button_ok)
                    btnOk.setOnClickListener {
                        dialog.dismiss()
                        switchLogin()
                    }
                    dialog.show()
                }
            }
            vm.error.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Helper.showDialogInfo(requireContext(), error)
                }
            }
            vm.loading.observe(viewLifecycleOwner) { state ->
                binding.loading.root.visibility = state
            }
        }
        binding.btnLogin.setOnClickListener {
            switchLogin()
        }
        binding.btnAction.setOnClickListener {
            /*
            *  NOTE REVIWER LALU :
            *  - untuk pengecekan logic tidak dilakukan di sini namun di file custom view
            *  - pengecekan disini -> jika input kosong tampilkan error field kosong
            *  - selain pengecekan field kosong -> tampilkan logic error dari custom view
            * */

            /* check if input is empty or not */
            if (binding.edName.text?.length ?: 0 <= 0) {
                binding.edName.error = getString(R.string.UI_validation_empty_name)
                binding.edName.requestFocus()
            } else if (binding.edEmail.text?.length ?: 0 <= 0) {
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
            } else if (binding.edName.error?.length ?: 0 > 0) {
                binding.edName.requestFocus()
            }
            /* not contain error */
            else {
                val name = binding.edName.text.toString()
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                viewModel.register(name, email, password)
            }
        }
    }

    private fun switchLogin() {
        /* while view models contains error -> clear error before replace fragments (to hide dialog error)*/
        viewModel.error.postValue("")

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
            /* shared element transition to main activity */
            addSharedElement(binding.labelAuth, "auth")
            addSharedElement(binding.edEmail, "email")
            addSharedElement(binding.edPassword, "password")
            addSharedElement(binding.containerMisc, "misc")
            commit()
        }
    }

}