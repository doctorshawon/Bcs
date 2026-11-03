package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BcsViewModel
import com.example.ui.Screen
import com.example.ui.theme.BcsEmeraldPrimary
import com.example.ui.theme.BcsGoldAccent
import com.example.ui.theme.WrongRed

data class SyllabusItem(
    val subject: String,
    val marks: Int,
    val details: String,
    val referenceBooks: String = ""
)

// 50th Special BCS Health Cadre Medical Sector (100 Marks)
val SpecialBcsMedicalSyllabus = listOf(
    SyllabusItem(
        subject = "১. সার্জারি ও জরুরি চিকিৎসা (Surgery & Emergency)",
        marks = 15,
        details = "General Surgery, Shock, Trauma & ATLS, Burns (Parkland formula), Acute Abdomen, Appendicitis, Cholelithiasis, Hernias, Intestinal Obstruction, Surgical Infection, Orthopedics, Anesthesia & Ophthalmology basics.",
        referenceBooks = "Bailey & Love's Short Practice of Surgery (28th Ed), ATLS Guidelines, BD Chaurasia / Vishram Singh Clinical Anatomy."
    ),
    SyllabusItem(
        subject = "২. মেডিসিন ও পেডিয়াট্রিক্স (Medicine & Pediatrics)",
        marks = 15,
        details = "Cardiovascular (MI, Heart Failure, HTN), Respiratory (COPD, Asthma, Pneumonia), Endocrinology (Diabetes & DKA), Neurology (Stroke, Meningitis), Nephrology (AKI, CKD), Infectious diseases (Malaria, Dengue, Enteric fever), IMCI Guidelines & Neonatal resuscitation.",
        referenceBooks = "Davidson's Principles and Practice of Medicine (24th Ed), Kumar & Clark's Clinical Medicine, WHO/UNICEF IMCI Protocol."
    ),
    SyllabusItem(
        subject = "৩. কমিউনিটি মেডিসিন, জনস্বাস্থ্য ও EPI (Community Medicine & EPI)",
        marks = 15,
        details = "Bangladesh National EPI Schedule (BCG, Pentavalent, PCV, MR, OPV, fIPV), Cold Chain Management, Reduced Osmolarity ORS (icddr,b), Primary Health Care (Alma-Ata & Astana), Demography, Epidemiology & Biostatistics, Maternal and Child Health (MCH), Water & Sanitation.",
        referenceBooks = "Park's Textbook of Preventive and Social Medicine (PSM), DGHS Bangladesh National EPI Manual, icddr,b Diarrhea Protocols."
    ),
    SyllabusItem(
        subject = "৪. ফার্মাকোলজি ও থেরাপিউটিক্স (Pharmacology & Therapeutics)",
        marks = 15,
        details = "Autonomic Nervous System (Adrenaline in Anaphylaxis, Atropine), Antimicrobial Chemotherapy & Antibiotic Stewardship, Cardiovascular drugs, NSAIDs & Analgesics, Antidiabetics, Antidotes (Beta-blockers, Paracetamol, Organophosphorus poisoning, Heparin/Warfarin).",
        referenceBooks = "Bertram Katzung Basic & Clinical Pharmacology, KD Tripathi Essentials of Medical Pharmacology."
    ),
    SyllabusItem(
        subject = "৫. অবস্টেট্রিক্স ও গাইনিকোলজি (Obstetrics & Gynecology)",
        marks = 10,
        details = "Antenatal Care (ANC), Normal Labor & Partograph, PPH (Postpartum Hemorrhage & Active Management of 3rd Stage of Labor), Preeclampsia & Eclampsia (Magnesium Sulfate protocol), Ectopic Pregnancy, Contraceptive methods.",
        referenceBooks = "DC Dutta's Textbook of Obstetrics & Gynecology, Ten Teachers Obstetrics, WHO ANC Guidelines."
    ),
    SyllabusItem(
        subject = "৬. প্যাথলজি ও মাইক্রোবায়োলজি (Pathology & Microbiology)",
        marks = 10,
        details = "Cellular adaptations, Acute & Chronic Inflammation, Granulomatous diseases (Tuberculosis, Leprosy), Neoplasia, Anemias & Leukemia, Bacteriology (Gram positive/negative cocci & bacilli), Virology (Hepatitis B & C, Dengue, HIV), Parasitology (Entamoeba, Giardia, Leishmania).",
        referenceBooks = "Robbins & Cotran Pathologic Basis of Disease, Ananthanarayan & Paniker's Textbook of Microbiology."
    ),
    SyllabusItem(
        subject = "৭. ফিজিওলজি ও বায়োকেমিস্ট্রি (Physiology & Biochemistry)",
        marks = 10,
        details = "Cardiovascular Physiology (Cardiac Output, BP regulation), Respiratory Physiology (Gas exchange), Renal Physiology (GFR, Acid-Base balance), Gastrointestinal Physiology, Neurophysiology, Carbohydrate & Lipid metabolism.",
        referenceBooks = "Guyton & Hall Textbook of Medical Physiology, Ganong's Review of Medical Physiology, Lippincott Illustrated Reviews: Biochemistry."
    ),
    SyllabusItem(
        subject = "৮. অ্যানাটমি ও নিউরোঅ্যানাটমি (Anatomy & Embryology)",
        marks = 5,
        details = "Gross Anatomy of Thorax, Abdomen, Pelvis, Head & Neck, Cranial Nerves, Blood supply of Brain (Circle of Willis), Popliteal fossa, Femoral triangle, Basic Embryology.",
        referenceBooks = "Vishram Singh Clinical Anatomy, A.K. Datta Essentials of Human Anatomy, Langman's Medical Embryology."
    ),
    SyllabusItem(
        subject = "৯. ফরেনসিক মেডিসিন ও টক্সিকোলজি (Forensic Medicine & Toxicology)",
        marks = 5,
        details = "Medical Jurisprudence, Post-mortem changes (Rigor Mortis, Algor Mortis, Livor Mortis), Mechanical injuries & wounds, Asphyxial deaths (Hanging, Strangulation, Drowning), Toxicology (OPC poisoning, Snake bite, Corrosives).",
        referenceBooks = "Parikh's Textbook of Medical Jurisprudence, Forensic Medicine and Toxicology, K.S. Narayan Reddy."
    )
)

// 50th Special BCS General Part (100 Marks)
val SpecialBcsGeneralSyllabus = listOf(
    SyllabusItem(
        subject = "১. বাংলা ভাষা ও সাহিত্য (Bengali Language & Literature)",
        marks = 20,
        details = "বাংলা ব্যাকরণ: প্রয়োগ-অপপ্রয়োগ, বানান ও বাক্য শুদ্ধি, সন্ধি, সমাস, প্রত্যয়, ধ্বনি ও বর্ণ; সাহিত্য: প্রাচীন যুগ (চর্যাপদ), মধ্যযুগ (মঙ্গলকাব্য, বৈষ্ণব পদাবলী, শ্রীকৃষ্ণকীর্তন), আধুনিক যুগ (ঈশ্বরচন্দ্র বিদ্যাসাগর, মাইকেল মধুসূদন দত্ত, বঙ্কিমচন্দ্র, রবীন্দ্রনাথ ঠাকুর, কাজী নজরুল ইসলাম, জসীমউদ্দীন, জীবনানন্দ দাশ ও মুক্তিযুদ্ধভিত্তিক সাহিত্য)।",
        referenceBooks = "ড. সৌমিত্র শেখর রচিত 'বাংলা ভাষা ও সাহিত্য জিজ্ঞাসা', ড. হায়াৎ মামুদ, নবম-দশম শ্রেণির মাধ্যমিক বাংলা ভাষার ব্যাকরণ ও নির্মিতি (NCTB)।"
    ),
    SyllabusItem(
        subject = "২. English Language & Literature",
        marks = 20,
        details = "Grammar & Usage: Parts of Speech, Idioms & Phrases, Clauses & Conditionals, Subject-Verb Agreement, Corrections, Vocabulary, Prepositions, Voice & Narration. Literature: Elizabethan & Jacobean periods (Shakespeare, Marlowe), Romantic Period (Wordsworth, Keats, Shelley), Victorian Period, Modern American & British Literature.",
        referenceBooks = "Wren & Martin's High School English Grammar & Composition, Cliff's TOEFL, Michael Swan's Practical English Usage, William J. Long's History of English Literature."
    ),
    SyllabusItem(
        subject = "৩. বাংলাদেশ বিষয়াবলি (Bangladesh Affairs)",
        marks = 20,
        details = "বাঙালির ইতিহাস ও স্বাধীনতা সংগ্রাম: ১৯৪৭ থেকে ১৯৭১ (ভাষা আন্দোলন, ৬ দফা, ১১ দফা, ঐতিহাসিক ৭ই মার্চের ভাষণ, মুক্তিযুদ্ধ ও মুজিবনগর সরকার)। গণপ্রজাতন্ত্রী বাংলাদেশের সংবিধান (প্রস্তাবনা, মৌলিক অধিকার, রাষ্ট্র পরিচালনার মূলনীতি, সংশোধনীসমূহ)। বাংলাদেশের ভূপ্রকৃতি, অর্থনৈতিক সমীক্ষা, কৃষি, স্বাস্থ্য খাত ও জনমিতি।",
        referenceBooks = "গণপ্রজাতন্ত্রী বাংলাদেশের সংবিধান (আইন মন্ত্রণালয়), বাংলাদেশ অর্থনৈতিক সমীক্ষা (অর্থ মন্ত্রণালয়), অধ্যাপক মুনতাসীর মামুন ও ড. আতিউর রহমান।"
    ),
    SyllabusItem(
        subject = "৪. আন্তর্জাতিক বিষয়াবলি (International Affairs)",
        marks = 20,
        details = "আন্তর্জাতিক ভূ-রাজনীতি, আন্তর্জাতিক নিরাপত্তা ও দ্বিপাক্ষিক সম্পর্ক, জাতিসংঘ ও এর সহযোগী সংস্থাসমূহ (WHO, UNICEF, UNESCO, ICRC), বৈশ্বিক পরিবেশ ও জলবায়ু চুক্তি (COP), আঞ্চলিক জোট (SAARC, BIMSTEC, ASEAN, NATO, BRICS), সাম্প্রতিক আন্তর্জাতিক ইস্যু ও স্বাস্থ্য কূটনীতি।",
        referenceBooks = "অধ্যাপক তারেক শামসুর রেহমান রচিত 'আন্তর্জাতিক রাজনীতি ও ভূ-রাজনীতি', জাতিসংঘ চার্টার, আন্তর্জাতিক স্বাস্থ্য বিষয়ক প্রকাশনা।"
    ),
    SyllabusItem(
        subject = "৫. গাণিতিক যুক্তি ও মানসিক দক্ষতা (Math & Mental Ability)",
        marks = 20,
        details = "গাণিতিক যুক্তি (১০ নম্বর): বাস্তব সংখ্যা, ল.সা.গু-গ.সা.গু, শতকরা, লাভ-ক্ষতি, সরল ও যৌগিক মুনাফা, অনুপাত, বীজগণিতীয় সূত্রাবলী, সূচক ও লগারিদম, জ্যামিতি। মানসিক দক্ষতা (১০ নম্বর): ভাষাগত যৌক্তিক বিচার, রক্তের সম্পর্ক, ঘড়ির কোণ ও সময়, দিক নির্ণয়, যান্ত্রিক দক্ষতা ও সংখ্যাগত পারদর্শিতা।",
        referenceBooks = "NCTB নবম-দশম শ্রেণির সাধারণ গণিত, খাইরুল'স বেসিক ম্যাথ (Khairul's Basic Math), জর্জ'স মানসিক দক্ষতা।"
    )
)

// General BCS 10 Subjects
val BcsSyllabusDetails = listOf(
    SyllabusItem("১. বাংলা ভাষা ও সাহিত্য", 35, "প্রয়োগ-অপপ্রয়োগ, বানান ও বাক্য শুদ্ধি, পরিভাষা, ধ্বনি, বর্ণ, শব্দ, পদ, সন্ধি, সমাস, প্রত্যয়; প্রাচীন ও মধ্যযুগ (৫ নম্বর); আধুনিক যুগ (১৫ নম্বর)", "ড. সৌমিত্র শেখর, NCTB ব্যাকরণ"),
    SyllabusItem("২. English Language & Literature", 35, "Parts of Speech, Idioms & Phrases, Clauses, Corrections, Vocabulary, Prepositions, Voice, Narration; Literature (15 marks): Shakespeare, Romantic, Victorian, Modern periods", "Wren & Martin, Cliff's TOEFL"),
    SyllabusItem("৩. বাংলাদেশ বিষয়াবলি", 30, "প্রাচীন কাল থেকে মুক্তিযুদ্ধ, ১৯৭২ সালের সংবিধান, সরকার ব্যবস্থা, জাতীয় অর্জন, অর্থনীতি, কৃষি, শিল্প ও বাণিজ্য, জনমিতি", "বাংলাদেশ সংবিধান, অর্থনৈতিক সমীক্ষা"),
    SyllabusItem("৪. আন্তর্জাতিক বিষয়াবলি", 20, "বৈশ্বিক ইতিহাস, আঞ্চলিক ও আন্তর্জাতিক ব্যবস্থা, ভূ-রাজনীতি, আন্তর্জাতিক নিরাপত্তা ও দ্বিপাক্ষিক সম্পর্ক, পরিবেশগত ইস্যু", "অধ্যাপক তারেক শামসুর রেহমান"),
    SyllabusItem("৫. ভূগোল, পরিবেশ ও দুর্যোগ ব্যবস্থাপনা", 10, "বাংলাদেশ ও বৈশ্বিক ভৌগোলিক অবস্থান, জলবায়ু পরিবর্তন, প্রাকৃতিক দুর্যোগ ও দুর্যোগ ব্যবস্থাপনা", "NCTB ভূগোল ও পরিবেশ"),
    SyllabusItem("৬. সাধারণ বিজ্ঞান", 15, "ভৌত বিজ্ঞান (আলো, শব্দ, চুম্বক, বিদ্যুৎ), জীববিজ্ঞান (কোষ, খাদ্য ও পুষ্টি, রক্ত, রোগবালাই), আধুনিক বিজ্ঞান", "NCTB সাধারণ বিজ্ঞান"),
    SyllabusItem("৭. কম্পিউটার ও তথ্যপ্রযুক্তি", 15, "কম্পিউটার সংগঠন ও পেরিফেরালস, মেমোরি, অপারেটিং সিস্টেম, ক্লাউড কম্পিউটিং, নেটওয়ার্কিং, সাইবার নিরাপত্তা", "HSC তথ্য ও যোগাযোগ প্রযুক্তি"),
    SyllabusItem("৮. গাণিতিক যুক্তি", 15, "বাস্তব সংখ্যা, ল.সা.গু-গ.সা.গু, শতকরা, সরল ও যৌগিক মুনাফা, অনুপাত, বীজগণিতীয় সূত্রাবলী, সমীকরণ, সূচক ও লগারিদম", "NCTB মাধ্যমিক গণিত"),
    SyllabusItem("৯. মানসিক দক্ষতা", 15, "ভাষাগত যৌক্তিক বিচার, সমস্যা সমাধান, বানান ও ভাষা, যান্ত্রিক দক্ষতা, স্থানাঙ্ক সম্পর্ক, সংখ্যাগত ক্ষমতা ও ঘড়ি", "খাইরুল'স মেন্টাল এবিলিটি"),
    SyllabusItem("১০. নৈতিকতা, মূল্যবোধ ও সুশাসন", 10, "সুশাসনের ধারণা ও উপাদান, মূল্যবোধের উৎস ও বিকাশ, জাতীয় জীবনে সততা ও নৈতিকতার ভূমিকা", "সুশাসন ও নৈতিকতা গাইড")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusGuideScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier.testTag("syllabus_back_btn")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "বিসিএস প্রিলিমিনারি সিলেবাস ও বুক গাইড",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Two-way Tabs: 50th Special BCS Health vs General BCS
        item {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = if (selectedTab == 0) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = if (selectedTab == 1) BcsEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সাধারণ বিসিএস সিলেবাস",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // TAB 0: 50TH SPECIAL BCS HEALTH CADRE SYLLABUS & TEXTBOOKS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F2)),
                    border = BorderStroke(1.5.dp, Color(0xFFFFCDD2))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য ক্যাডার) পূর্ণাঙ্গ সিলেবাস",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFB71C1C)
                                )
                                Text(
                                    text = "২০০ নম্বরের প্রিলিমিনারি পরীক্ষা কাঠামো ও প্রামাণ্য গ্রন্থাবলী",
                                    fontSize = 11.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "মেডিকেল সেক্টর", fontSize = 11.sp, color = Color(0xFF5D4037))
                                Text(text = "১০০ নম্বর", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFFB71C1C))
                            }
                            Column {
                                Text(text = "জেনারেল পার্ট", fontSize = 11.sp, color = Color(0xFF5D4037))
                                Text(text = "১০০ নম্বর", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFFB71C1C))
                            }
                            Column {
                                Text(text = "সময়", fontSize = 11.sp, color = Color(0xFF5D4037))
                                Text(text = "১২০ মিনিট", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFFB71C1C))
                            }
                            Column {
                                Text(text = "নেগেটিভ মার্ক", fontSize = 11.sp, color = Color(0xFF5D4037))
                                Text(text = "-০.৫ প্রতি ভুল", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = WrongRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons in Hero
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.start50thSpecialBcsModelTest(200) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("syllabus_start_200m_test_btn")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("২০০ নম্বরের টেস্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.MAKE_MCQ) },
                                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("syllabus_make_ai_mcq_btn")
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI প্রশ্ন তৈরি", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section 1 Header: Medical Sector (100 Marks)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "মেডিকেল সেক্টর (১০০ নম্বর)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB71C1C)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD32F2F)
                    ) {
                        Text(
                            text = "১০০ MCQ",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            items(SpecialBcsMedicalSyllabus) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.subject,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFB71C1C),
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF2F2)
                            ) {
                                Text(
                                    text = "${item.marks} নম্বর",
                                    color = Color(0xFFB71C1C),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.details,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )

                        if (item.referenceBooks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BcsGoldAccent.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, BcsGoldAccent.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📚 রেফারেন্স গ্রন্থ: ${item.referenceBooks}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF5D4037),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2 Header: General Part (100 Marks)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BcsEmeraldPrimary,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "জেনারেল পার্ট (১০০ নম্বর)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BcsEmeraldPrimary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BcsEmeraldPrimary
                    ) {
                        Text(
                            text = "১০০ MCQ",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            items(SpecialBcsGeneralSyllabus) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.subject,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BcsEmeraldPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${item.marks} নম্বর",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.details,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )

                        if (item.referenceBooks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📚 রেফারেন্স গ্রন্থ: ${item.referenceBooks}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TAB 1: GENERAL BCS 10 SUBJECTS SYLLABUS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "সাধারণ বিসিএস প্রিলিমিনারি পরীক্ষা কাঠামো (১০টি বিষয়)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "মোট প্রশ্ন", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(text = "২০০ টি MCQ", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BcsEmeraldPrimary)
                            }
                            Column {
                                Text(text = "মোট নম্বর", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(text = "২০০ নম্বর", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BcsEmeraldPrimary)
                            }
                            Column {
                                Text(text = "মোট সময়", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(text = "২ ঘণ্টা (১২০ মি.)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BcsEmeraldPrimary)
                            }
                            Column {
                                Text(text = "নেগেটিভ মার্ক", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(text = "-০.৫ প্রতি ভুল", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = WrongRed)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "১০টি বিষয়ের পূর্ণ নম্বর বণ্টন ও পরিধি",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(BcsSyllabusDetails) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.subject,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BcsEmeraldPrimary
                            ) {
                                Text(
                                    text = "${item.marks} নম্বর",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.details,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                        if (item.referenceBooks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📚 রেফারেন্স: ${item.referenceBooks}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
