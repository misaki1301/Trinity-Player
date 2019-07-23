package com.shibuyaxpress.trinity_player.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.shibuyaxpress.trinity_player.R

class MercadoPagoFragment : Fragment() {

    private val REQUEST_CODE: Int? = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mercado_pago, container, false)
        // Inflate the layout for this fragment
        val mercadopagoButton = rootView.findViewById<Button>(R.id.mercado_pago_button)
        mercadopagoButton.setOnClickListener {
            makePayWithMercadoPago("175779689-9a2ec53c-9e5c-4ed7-b995-3e589ec13e00")
        }
        return rootView
    }

    private fun makePayWithMercadoPago(checkoutPreferenceId: String?){
        val payment = MercadoPagoCheckout.Builder("TEST-dedef75d-7d70-4491-926b-cc2ff3eaeceb", checkoutPreferenceId!!)
            .build()
        payment.startPayment(activity!!.applicationContext,REQUEST_CODE!!)
    }


}
