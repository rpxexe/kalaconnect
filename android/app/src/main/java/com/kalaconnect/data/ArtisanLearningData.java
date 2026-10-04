package com.kalaconnect.data;

import com.kalaconnect.models.LearningModule;
import com.kalaconnect.models.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArtisanLearningData {

    public static List<LearningModule> getLearningModules() {
        List<LearningModule> modules = new ArrayList<>();

        // Module 1: Craft Photography
        modules.add(new LearningModule(
                "mod_photo",
                "Smartphone Photography for Handcrafted Products",
                "Capture studio-grade craft listings using natural sunlight and basic smartphone tools.",
                "CATALOGING",
                "6 min study",
                "6 min",
                "Essential",
                "Learn how to photograph handmade items with true-to-life colors, distinct textures, and compelling angles that convince conscious global buyers.",
                Arrays.asList(
                        "Shoot near a window or shaded courtyard with soft, indirect daylight.",
                        "Never use direct smartphone LED flash — it washes out natural vegetable dyes and clay patina.",
                        "Always take 3 mandatory shots: 1) Full Hero Angle, 2) Macro Texture/Weave Detail, 3) Hand-Held Scale.",
                        "Use a clean white, linen, or neutral earthen background to keep focus purely on your craft."
                ),
                "Handcrafted items sell on authentic detail. When customers buy online, they want to feel the texture of the fabric or the glaze of the clay through your lens.\n\n" +
                        "1. Lighting: Position your craft 3 to 5 feet away from a window during morning (8 AM - 10 AM) or late afternoon (4 PM - 5 PM). Diffused natural light prevents harsh black shadows.\n\n" +
                        "2. Stability: Keep your phone level. Lean your elbows on the table or use a simple mobile tripod to eliminate blur.\n\n" +
                        "3. Clean Backdrop: A simple unstarched cotton sheet or wooden table works wonders. Avoid cluttered backgrounds with wires or plastic bottles.\n\n" +
                        "4. Macro Detailing: Move the phone camera 4 inches close to showcase embroidery stitches, terracotta carve-marks, or metal chiseling.",
                "Tip: Wipe your phone camera lens with a soft cotton cloth before every photo session. Pocket smudges ruin 40% of craft photos!"
        ));

        // Module 2: Fair Wage & Pricing
        modules.add(new LearningModule(
                "mod_pricing",
                "Craft Costing: Never Underprice Your Heritage",
                "Calculate accurate pricing factoring in skilled labour hours, raw materials, and fair profit.",
                "FINANCE",
                "8 min study",
                "8 min",
                "High Value",
                "Stop selling yourself short. Learn the standard KalaConnect formula to value every hour of master craftsmanship and protect your household income.",
                Arrays.asList(
                        "Artisan Formula = Raw Material + (Artisan Hourly Rate × Hours) + Workshop Fuel/Tools + 20% SHG Growth Fund.",
                        "Always include time spent preparing clay, dying threads, or sketching motifs.",
                        "Do not compete with factory-made mass plastic; price for heritage, rarity, and lifetime durability.",
                        "Separate wholesale bulk rates (for NGOs/stores) from direct single-item retail rates."
                ),
                "Many rural artisans calculate only the cost of raw clay, yarn, or brass, effectively working for pennies per hour. This perpetuates poverty.\n\n" +
                        "1. Setting Your Hourly Rate: Decide a fair living wage (e.g., ₹120 to ₹200 per hour depending on mastery level).\n\n" +
                        "2. Tracking Total Crafting Time: Count preparatory work (clay kneading, warping looms, drying time) plus core making hours.\n\n" +
                        "3. Material Buffer: Add 10% for material wastage or firing breakage in kilns.\n\n" +
                        "4. Sustainable Profit Margin: Always retain at least 20% profit to reinvest in newer toolkits, healthcare, and children's education.",
                "Tip: When buyers negotiate aggressively, explain the number of human hours dedicated to handcrafting the single piece rather than dropping the price instantly."
        ));

        // Module 3: GI Tags & Heritage IP
        modules.add(new LearningModule(
                "mod_gi",
                "GI Tags: Protecting Indigenous Art from Factory Copies",
                "Understand Geographical Indications (GI), legal protection, and authenticity branding.",
                "HERITAGE & IP",
                "5 min study",
                "5 min",
                "Heritage",
                "Discover how authorized Geographical Indication (GI) tags protect authentic regional crafts (like Madhubani, Gorakhpur Terracotta, Chanderi) against cheap industrial imitations.",
                Arrays.asList(
                        "A GI Tag is a recognized intellectual property right linked to a specific geographical territory.",
                        "Protects craft communities against counterfeit factory machine prints.",
                        "Enables artisans to command 30% to 50% higher price premiums in domestic and export markets.",
                        "Registration as an 'Authorized User' is accessible through state handloom and handicraft boards."
                ),
                "Counterfeit goods harm indigenous artisans. Powerlooms and 3D printing often duplicate traditional tribal motifs and sell them cheaply as authentic handlooms.\n\n" +
                        "1. What is a GI Tag? It certifies that a craft possesses qualities and reputation attributable only to its origin location (e.g., Gorakhpur Terracotta, Kutch Embroidery, Pochampally Ikat).\n\n" +
                        "2. Authorized User Status: Individual artisans and SHG clusters can register under their regional registered GI proprietor with assistance from partner NGOs.\n\n" +
                        "3. Authenticity Seals: Affixing the official GI logo or KalaConnect QR provenance tag builds instant trust with overseas and corporate buyers.",
                "Tip: Always include your village name and cluster origin in your product descriptions — provenance is your strongest selling asset!"
        ));

        // Module 4: Packaging & Safe Transit
        modules.add(new LearningModule(
                "mod_packaging",
                "Zero-Breakage Packaging for Domestic & Export Shipping",
                "Pack delicate terracotta, stone, brass, and silk fabrics safely against rain and rough transport.",
                "LOGISTICS",
                "7 min study",
                "7 min",
                "Operations",
                "Ensure your delicate masterpieces reach customers safely across India and worldwide without cracks, moisture stains, or courier transit damage.",
                Arrays.asList(
                        "The 'Double Box' rule: Cushion fragile pottery inside an inner box, separated by 2 inches from the outer shipping carton.",
                        "Moisture Protection: Seal handlooms and paintings inside biodegradable waterproof poly sleeves with silica gel sachets.",
                        "Corner Protection: Reinforce box corners with folded corrugated cardboard inserts.",
                        "Clear Labeling: Mark 'FRAGILE - THIS SIDE UP' on all four sides in both English and Hindi."
                ),
                "Transit damage leads to refunds, lost profits, and disappointed patrons. A sound packaging strategy costs very little but saves entire consignments.\n\n" +
                        "1. For Terracotta & Ceramics: Wrap individual items in 3 layers of honeycomb paper or bubble wrap. Fill voids inside vases with crumpled newsprint so the hollow center doesn't collapse.\n\n" +
                        "2. The Shake Test: Once packed, gently shake the sealed box. If you feel or hear anything shifting, open it and add more cushioning.\n\n" +
                        "3. Handlooms & Textiles: Never pack damp fabrics. Always air-dry completely. Include eco-friendly dried neem leaves or silica packets to prevent fungal odors during monsoon courier transit.",
                "Tip: Take a quick 5-second video of the item being packed before sealing. This acts as concrete proof if a shipping partner damages the shipment."
        ));

        // Module 5: PM Vishwakarma & Government Schemes
        modules.add(new LearningModule(
                "mod_schemes",
                "Government Schemes: PM Vishwakarma & MUDRA Loans",
                "Unlock low-interest capital, modern toolkit incentives, and skill stipends from the government.",
                "GOVERNMENT",
                "9 min study",
                "9 min",
                "Finance",
                "Learn how to apply for PM Vishwakarma Yojana, MUDRA Shishu loans, and National Handicrafts Development schemes designed specifically for traditional craftspeople.",
                Arrays.asList(
                        "PM Vishwakarma provides collateral-free enterprise loans up to ₹3 Lakh at just 5% interest.",
                        "Receive ₹15,000 direct benefit transfer for purchasing modern artisan toolkits.",
                        "5 to 7 days basic skill upgradation training with ₹500/day daily stipend.",
                        "Nationally recognized PM Vishwakarma Certificate and Digital ID Card."
                ),
                "The Government of India has introduced unprecedented direct-support policies to recognize artisans as economic nation-builders.\n\n" +
                        "1. PM Vishwakarma Yojana: Covers 18 traditional trades including potters, weavers, blacksmiths, sculptors, and basket makers. Apply via your local Common Services Center (CSC) with Aadhaar and bank passbook.\n\n" +
                        "2. Loan Tranches: Tranche 1 provides ₹1,00,000 for 18 months. On timely repayment, Tranche 2 unlocks ₹2,00,000 for 30 months at a subsidized 5% interest rate.\n\n" +
                        "3. Digital Incentive: Earn ₹1 per digital transaction up to 100 transactions each month to promote online payments.\n\n" +
                        "4. Marketing Support: National Handicrafts Development Programme (NHDP) provides free stalls at Dastkar, Surajkund, and Saras Melas.",
                "Tip: Partner with your registered KalaConnect NGO partner to file your PM Vishwakarma verification through your Gram Panchayat or Urban Local Body."
        ));

        // Module 6: Digital Storytelling
        modules.add(new LearningModule(
                "mod_digital",
                "Digital Storytelling: Connecting with Conscious Buyers",
                "Transform casual scrollers into lifelong patrons by sharing the cultural story of your making.",
                "MARKETING",
                "6 min study",
                "6 min",
                "Growth",
                "Modern conscious consumers do not just purchase an item; they invest in your cultural heritage, artisan family lineage, and eco-friendly slow living.",
                Arrays.asList(
                        "Explain what makes your craft unique: natural clay, vegetable dyes, ancestral motifs.",
                        "Share short 15-second video clips of the wheel turning or loom shuttle moving.",
                        "Introduce the master artisan behind each piece with name and years of experience.",
                        "Respond to buyer enquiries politely and promptly within 2 to 4 hours."
                ),
                "In an era of mass-produced plastic, authentic human artistry is precious. Your story is your most valuable competitive advantage.\n\n" +
                        "1. Who made it? Buyers love knowing that Smt. Meera Bai from Gorakhpur shaped the clay pot by hand on a foot-pedal wheel.\n\n" +
                        "2. Where does it come from? Mention local earth, sacred rivers, or forested timber from your native region.\n\n" +
                        "3. Use Simple WhatsApp Catalogs: Organize your products with clear prices so buyers can forward your creations to their friends and family.",
                "Tip: When packing an order, include a small handwritten postcard saying 'Namaste, thank you for supporting traditional Indian artisans.' Buyers share this on social media!"
        ));

        return modules;
    }

    public static List<QuizQuestion> getQuizQuestions() {
        List<QuizQuestion> questions = new ArrayList<>();

        // Question 1: Fair Costing
        questions.add(new QuizQuestion(
                "q1",
                "When calculating the selling price of a handcrafted item, which essential cost is most often forgotten by rural artisans?",
                Arrays.asList(
                        "Cost of courier tape and wrapping string",
                        "The artisan's skilled labour hours at a fair living wage",
                        "The electric recharge of their smartphone",
                        "The color of the delivery receipt"
                ),
                1,
                "Rural artisans frequently count only physical raw materials (clay, thread, brass) and omit their valuable labour hours. Skilled time must always be compensated at a fair hourly living wage!",
                "Fair Pricing & Living Wages"
        ));

        // Question 2: Lighting
        questions.add(new QuizQuestion(
                "q2",
                "What is the best natural lighting condition for photographing intricate handloom sarees and terracotta crafts?",
                Arrays.asList(
                        "Direct harsh midday sun outdoors in the open",
                        "Pitch dark room with phone's direct LED flashlight",
                        "Soft, indirect natural daylight near a window or shaded courtyard",
                        "Under colored festive party bulbs"
                ),
                2,
                "Soft, indirect daylight illuminates the craft evenly without casting harsh shadows or bleaching the subtle tones of natural dyes and clay.",
                "Catalog Photography"
        ));

        // Question 3: GI Tags
        questions.add(new QuizQuestion(
                "q3",
                "What major legal and economic advantage does a Geographical Indication (GI) Tag provide to your artisan community?",
                Arrays.asList(
                        "Legal protection against factory-made copies and right to heritage pricing",
                        "Free airline tickets for all village residents",
                        "Compulsory requirement to sell only to government agents",
                        "Permission to use chemical synthetic dyes"
                ),
                0,
                "A GI Tag grants legal intellectual property protection against industrial machine-made counterfeits and allows authentic artisans to command premium heritage prices.",
                "GI Tags & Heritage Protection"
        ));

        // Question 4: Packaging
        questions.add(new QuizQuestion(
                "q4",
                "How should fragile terracotta or ceramic crafts be packaged to guarantee zero breakage during courier delivery?",
                Arrays.asList(
                        "A single lightweight plastic grocery bag",
                        "Double-boxing with 2 inches of cushioning separating the inner item from the outer carton",
                        "Loose inside a paper envelope",
                        "Wrapped in a single layer of newspaper with no box"
                ),
                1,
                "The double-box technique cushions against drops and pressure during long-distance road and air courier handling.",
                "Safe Transit & Packaging"
        ));

        // Question 5: PM Vishwakarma
        questions.add(new QuizQuestion(
                "q5",
                "Under the PM Vishwakarma Yojana, what financial benefits are provided to traditional craftspeople?",
                Arrays.asList(
                        "Collateral-free enterprise credit up to ₹3 Lakh at 5% interest + ₹15,000 toolkit support",
                        "Mandatory stock market investment scheme",
                        "Only high-interest personal credit cards",
                        "Obligation to surrender traditional tools"
                ),
                0,
                "PM Vishwakarma provides collateral-free loans in two tranches (₹1L + ₹2L) at a subsidized 5% interest rate, plus ₹15,000 grant for modern toolkits and skill training stipends.",
                "Government Schemes & Subsidies"
        ));

        return questions;
    }

    public static String getDailyCraftTip() {
        return "Always photograph your craft from 3 angles: The Hero Full View, The Macro Texture, and In-Hand for Scale. Natural morning light near a window gives the truest vegetable dye colors!";
    }
}
