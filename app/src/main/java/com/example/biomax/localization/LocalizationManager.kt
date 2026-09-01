package com.example.biomax.localization

import com.example.biomax.model.AppLanguage
import com.example.biomax.model.CurrencyUnit
import java.util.Locale

object LocalizationManager {

    private val translations = mapOf(
        "app_tagline" to mapOf(
            AppLanguage.EN to "Quick-Commerce Food Waste to Clean Biogas Marketplace",
            AppLanguage.ES to "Mercado de Comercio Rápido de Residuos a Biogás Limpio",
            AppLanguage.DE to "Quick-Commerce Marktplatz für Speisereste zu sauberem Biogas",
            AppLanguage.FR to "Marché Quick-Commerce de Valorisation des Déchets Alimentaires en Biogaz",
            AppLanguage.HI to "खाद्य अपशिष्ट से स्वच्छ बायोगैस त्वरित वाणिज्य बाज़ार",
            AppLanguage.ZH to "餐厨废弃物快速转化为清洁沼气能源交易平台",
            AppLanguage.JA to "食品廃棄物からクリーンバイオガスへのクイックコマース取引所"
        ),
        "tab_marketplace" to mapOf(
            AppLanguage.EN to "Marketplace",
            AppLanguage.ES to "Mercado",
            AppLanguage.DE to "Marktplatz",
            AppLanguage.FR to "Marché",
            AppLanguage.HI to "बाज़ार (Market)",
            AppLanguage.ZH to "原料交易市集",
            AppLanguage.JA to "原料マーケット"
        ),
        "tab_restaurant" to mapOf(
            AppLanguage.EN to "My Kitchen Lots",
            AppLanguage.ES to "Mis Lotes",
            AppLanguage.DE to "Meine Chargen",
            AppLanguage.FR to "Mes Lots Cuisine",
            AppLanguage.HI to "रसोई अपशिष्ट लॉट",
            AppLanguage.ZH to "餐厅废弃物批次",
            AppLanguage.JA to "レストラン在庫"
        ),
        "tab_logistics" to mapOf(
            AppLanguage.EN to "Live Logistics",
            AppLanguage.ES to "Logística en Vivo",
            AppLanguage.DE to "Live-Logistik",
            AppLanguage.FR to "Logistique en Direct",
            AppLanguage.HI to "लाइव लॉजिस्टिक्स",
            AppLanguage.ZH to "实时运力追踪",
            AppLanguage.JA to "リアルタイム物流"
        ),
        "tab_analytics" to mapOf(
            AppLanguage.EN to "Analytics & Escrow",
            AppLanguage.ES to "Analítica y Depósito",
            AppLanguage.DE to "Analytik & Treuhand",
            AppLanguage.FR to "Analyses & Séquestre",
            AppLanguage.HI to "एनालिटिक्स और एस्क्रो",
            AppLanguage.ZH to "数据分析与结算",
            AppLanguage.JA to "分析と決済"
        ),
        "tab_security" to mapOf(
            AppLanguage.EN to "Security & MFA",
            AppLanguage.ES to "Seguridad y MFA",
            AppLanguage.DE to "Sicherheit & MFA",
            AppLanguage.FR to "Sécurité & MFA",
            AppLanguage.HI to "सुरक्षा और MFA",
            AppLanguage.ZH to "安全防护与合规",
            AppLanguage.JA to "セキュリティ・MFA"
        ),
        "role_restaurant" to mapOf(
            AppLanguage.EN to "Restaurant Mode",
            AppLanguage.ES to "Modo Restaurante",
            AppLanguage.DE to "Restaurant-Modus",
            AppLanguage.FR to "Mode Restaurant",
            AppLanguage.HI to "रेस्टोरेंट मोड",
            AppLanguage.ZH to "餐饮商家视角",
            AppLanguage.JA to "飲食店モード"
        ),
        "role_biogas" to mapOf(
            AppLanguage.EN to "Biogas Facility Mode",
            AppLanguage.ES to "Modo Planta Biogás",
            AppLanguage.DE to "Biogasanlagen-Modus",
            AppLanguage.FR to "Mode Usine Biogaz",
            AppLanguage.HI to "बायोगैस संयंत्र मोड",
            AppLanguage.ZH to "沼气电站买家",
            AppLanguage.JA to "バイオガス発電所モード"
        ),
        "instant_buy" to mapOf(
            AppLanguage.EN to "Procure Lot (Escrow Lock)",
            AppLanguage.ES to "Comprar Lote (Garantía Escrow)",
            AppLanguage.DE to "Charge beschaffen (Treuhand)",
            AppLanguage.FR to "Acheter le Lot (Séquestre)",
            AppLanguage.HI to "लॉट खरीदें (सुरक्षित एस्क्रो)",
            AppLanguage.ZH to "立即锁定求购 (担保托管)",
            AppLanguage.JA to "ロット即時買付 (エスクロー)"
        ),
        "post_waste_lot" to mapOf(
            AppLanguage.EN to "Post Food Waste Lot",
            AppLanguage.ES to "Publicar Lote de Residuos",
            AppLanguage.DE to "Speiserest-Posten anlegen",
            AppLanguage.FR to "Publier un Lot de Déchets",
            AppLanguage.HI to "नया अपशिष्ट लॉट पोस्ट करें",
            AppLanguage.ZH to "发布新的厨余废弃物",
            AppLanguage.JA to "食品廃棄物を出品する"
        ),
        "methane_yield" to mapOf(
            AppLanguage.EN to "Est. Methane Yield",
            AppLanguage.ES to "Rendimiento Estimado Metano",
            AppLanguage.DE to "Geschätzter Methanertrag",
            AppLanguage.FR to "Rendement Méthane Estimé",
            AppLanguage.HI to "अनुमानित मीथेन उत्पादन",
            AppLanguage.ZH to "预估产气量 (CH₄)",
            AppLanguage.JA to "予想メタン産生量"
        ),
        "energy_output" to mapOf(
            AppLanguage.EN to "Clean Electrical Energy",
            AppLanguage.ES to "Energía Eléctrica Limpia",
            AppLanguage.DE to "Saubere elektrische Energie",
            AppLanguage.FR to "Énergie Électrique Propre",
            AppLanguage.HI to "स्वच्छ विद्युत ऊर्जा",
            AppLanguage.ZH to "清洁发电量",
            AppLanguage.JA to "クリーン電力発電"
        ),
        "co2_abatement" to mapOf(
            AppLanguage.EN to "CO₂ Landfill Diversion",
            AppLanguage.ES to "Desvío de CO₂ de Vertedero",
            AppLanguage.DE to "CO₂-Deponieeinsparung",
            AppLanguage.FR to "Évitement CO₂ Enfouissement",
            AppLanguage.HI to "CO₂ लैंडफिल न्यूनीकरण",
            AppLanguage.ZH to "填埋减排碳减免 (CO₂e)",
            AppLanguage.JA to "CO₂ 埋立削減量"
        ),
        "quality_score" to mapOf(
            AppLanguage.EN to "Quality & Purity Rating",
            AppLanguage.ES to "Calificación de Calidad y Pureza",
            AppLanguage.DE to "Qualitäts- & Reinheitsbewertung",
            AppLanguage.FR to "Note de Pureté & Qualité",
            AppLanguage.HI to "गुणवत्ता और शुद्धता रेटिंग",
            AppLanguage.ZH to "原料纯净度评分",
            AppLanguage.JA to "品質・純度スコア"
        ),
        "security_badge" to mapOf(
            AppLanguage.EN to "AES-256 E2E Encrypted & RSA Signed",
            AppLanguage.ES to "Cifrado E2E AES-256 y Firma RSA",
            AppLanguage.DE to "AES-256 E2E-verschlüsselt & RSA-signiert",
            AppLanguage.FR to "Chiffré E2E AES-256 et Signé RSA",
            AppLanguage.HI to "AES-256 एंड-टू-एंड एन्क्रिप्टेड",
            AppLanguage.ZH to "AES-256 端到端加密与数字签名",
            AppLanguage.JA to "AES-256 暗号化 & RSA 署名済み"
        ),
        "mfa_verified" to mapOf(
            AppLanguage.EN to "Multi-Factor Authentication Active",
            AppLanguage.ES to "Autenticación Multifactor Activa",
            AppLanguage.DE to "Multi-Faktor-Authentifizierung aktiv",
            AppLanguage.FR to "Authentification Multifacteur Active",
            AppLanguage.HI to "मल्टी-फैक्टर प्रमाणीकरण सक्रिय",
            AppLanguage.ZH to "多因素双重身份验证保护中",
            AppLanguage.JA to "二要素認証 (MFA) 有効"
        )
    )

    fun getString(key: String, language: AppLanguage): String {
        return translations[key]?.get(language)
            ?: translations[key]?.get(AppLanguage.EN)
            ?: key
    }

    fun formatPrice(usdAmount: Double, currency: CurrencyUnit): String {
        val converted = usdAmount * currency.rateFromUsd
        return if (currency == CurrencyUnit.JPY || currency == CurrencyUnit.INR) {
            "${currency.symbol}${String.format(Locale.US, "%,.0f", converted)}"
        } else {
            "${currency.symbol}${String.format(Locale.US, "%,.2f", converted)}"
        }
    }

    fun formatWeight(kg: Double, useTons: Boolean = false): String {
        return if (useTons || kg >= 1000.0) {
            String.format(Locale.US, "%.2f Metric Tons", kg / 1000.0)
        } else {
            String.format(Locale.US, "%.0f kg", kg)
        }
    }
}
