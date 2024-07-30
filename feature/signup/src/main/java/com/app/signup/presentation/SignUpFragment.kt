package com.app.signup.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.signup.R
import com.app.signup.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextRegisterEmail.text.toString().trim()
            val password = binding.editTextRegisterPassword.text.toString().trim()
            signUpViewModel.signUp(email, password)
        }

        observeSignUpState()
    }

    private fun observeSignUpState() {
        lifecycleScope.launch {
            signUpViewModel.uiState.collect { state ->
                binding.progressBarSignUp.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                if (state.success) {
                    Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                }

                state.error?.let { errorMessage ->
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}