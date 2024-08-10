package com.app.signup.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.domain.model.Gender
import com.app.domain.model.User
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
            val heightStr = binding.editTextRegisterHeight.text.toString().trim()
            val weightStr = binding.editTextRegisterWeight.text.toString().trim()
            val ageStr = binding.editTextRegisterAge.text.toString().trim()
            val gender = if (binding.radioButtonMale.isChecked) Gender.MALE else Gender.FEMALE

            if (email.isEmpty()) {
                Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (heightStr.isEmpty()) {
                Toast.makeText(context, "Height cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (weightStr.isEmpty()) {
                Toast.makeText(context, "Weight cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageStr.isEmpty()) {
                Toast.makeText(context, "Age cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightValue = heightStr.toDoubleOrNull()
            val weightValue = weightStr.toDoubleOrNull()
            val ageValue = ageStr.toIntOrNull()

            if (heightValue == null || weightValue == null || ageValue == null) {
                Toast.makeText(context, "Invalid height, weight, or age value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                email = email,
                height = heightValue,
                weight = weightValue,
                age = ageValue,
                gender = gender
            )

            // Call the signUp method with proper types
            signUpViewModel.signUp(email, password, heightValue, weightValue, ageValue, gender)
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
