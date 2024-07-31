package com.app.signin.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.signin.R
import com.app.signin.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.action_signInFragment_to_navigation_home)
        }
      */
        binding.buttonRegister.setOnClickListener{
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }


        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextLoginEmail.text.toString().trim()
            val password = binding.editTextLoginPassword.text.toString().trim()
            signInViewModel.signIn(email, password)
            observeSignInState()
        }
    }

    private fun observeSignInState() {
        lifecycleScope.launch {
            signInViewModel.uiState.collect { state ->
                binding.progressBarSignIn.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                if (state.success) {
                    Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signInFragment_to_navigation_home)
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


