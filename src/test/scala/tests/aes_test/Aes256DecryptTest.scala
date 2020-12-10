// Copyright 2020 Anish Singhani
//
// SPDX-License-Identifier: Apache-2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package tests.aes_test

import aes.AesCombined
import chisel3.iotesters.PeekPokeTester

class Aes256DecryptTest(dut: AesCombined) extends PeekPokeTester(dut) {

    def setTopKey(key: BigInt): Unit = {
        while(peek(dut.io.decReady) == 0) step(1)

        poke(dut.io.decDataValid, false)
        poke(dut.io.keyShift, false)
        expect(dut.io.decReady, true)

        poke(dut.io.keyIn, key)
        poke(dut.io.keyShift, true)
        step(1)
        poke(dut.io.keyIn, 0)
        poke(dut.io.keyShift, false)

        while(peek(dut.io.decReady) == 0) step(1)
    }

    def setKey(key: BigInt): Unit = {
        while(peek(dut.io.decReady) == 0) step(1)

        poke(dut.io.decDataValid, false)
        poke(dut.io.keyValid, false)
        expect(dut.io.decReady, true)

        poke(dut.io.keyIn, key)
        poke(dut.io.keyValid, true)
        step(1)
        poke(dut.io.keyIn, 0)
        poke(dut.io.keyValid, false)

        while(peek(dut.io.decReady) == 0) step(1)
    }

    def runTest(ciphertext: BigInt, plaintext: BigInt, iv: BigInt = BigInt(0)): Unit = {
        poke(dut.io.decIvIn, iv)
        poke(dut.io.decDataIn, ciphertext)
        poke(dut.io.decDataValid, true)
        step(1)
        poke(dut.io.decDataIn, 0)
        poke(dut.io.decDataValid, false)

        expect(dut.io.decReady, false)

        while(peek(dut.io.decOutputValid) == 0) step(1)

        expect(dut.io.decDataOut, plaintext)
        expect(dut.io.decIvOut, ciphertext)
    }

    poke(dut.io.aes256, true)
    poke(dut.io.keyValid, false)
    poke(dut.io.keyShift, false)
    poke(dut.io.encDataValid, false)
    poke(dut.io.decDataValid, false)

    setTopKey(BigInt("129445976579865719297921356551604413220"))
    setKey(BigInt("162796526750907890493247941453607545396"))
    runTest(BigInt("59925632134564593020758952134513872930"), BigInt("138766332635719238849554048983485396278"))
    runTest(BigInt("59925632134564593020758952134513872930"), BigInt("138766332635719238849554048983485396278"))
    runTest(BigInt("59925632134564593020758952134513872930"), BigInt("138766332635719238849554048983485396278"))

    setTopKey(BigInt("43076616058772830247523367216223830330"))
    setKey(BigInt("86828045678476342984355701054975456293"))
    runTest(BigInt("270932460674669383226104909649427376529"), BigInt("83046496474589931610202698648851854656"))
    runTest(BigInt("99566517636885309170435922759266722752"), BigInt("83046496474589931610202698648851854656"), iv=BigInt("140219081604921992313470763312941324362"))
    runTest(BigInt("78519063936877181953876667475887854997"), BigInt("75938425580371595357530013457038407522"))
    runTest(BigInt("206115656974254085712266147038936215728"), BigInt("75938425580371595357530013457038407522"), iv=BigInt("65756244442331551138503660245490672683"))
    runTest(BigInt("123650076469529813953811584850111161057"), BigInt("45434571344857213552920578958946087770"))
    runTest(BigInt("298869013917286730811956339591862009885"), BigInt("45434571344857213552920578958946087770"), iv=BigInt("158823583405137210771856377671607475268"))
    runTest(BigInt("168985546990840158403201783412762646194"), BigInt("99880003785891463167914823613802238531"))
    runTest(BigInt("111106102289974377900240273703620866538"), BigInt("99880003785891463167914823613802238531"), iv=BigInt("158729289693470940356364974982467188522"))
    runTest(BigInt("158390536994722892102477850581574648225"), BigInt("91915686096296336187203045313631631192"))
    runTest(BigInt("293969755379990597860618483306041478105"), BigInt("91915686096296336187203045313631631192"), iv=BigInt("163834797305842778657133365069205348438"))
    runTest(BigInt("187191027736075297980750320539812669048"), BigInt("113437637170993128078440790153014751082"))
    runTest(BigInt("295422956158761766087404478086280458249"), BigInt("113437637170993128078440790153014751082"), iv=BigInt("135910888095920255252074418598000142700"))
    runTest(BigInt("325675404707296609144275718021836792645"), BigInt("53783094394519968591621160242778683964"))
    runTest(BigInt("81047702847809599747300263640336659914"), BigInt("53783094394519968591621160242778683964"), iv=BigInt("132108892883998597353550447396540476230"))
    runTest(BigInt("277422276324047129291785012380454005877"), BigInt("48352460278305044537947291407016490023"))
    runTest(BigInt("191987313536236493584834387657497089452"), BigInt("48352460278305044537947291407016490023"), iv=BigInt("108074944374197834817594057603065410339"))
    runTest(BigInt("186828996244483812398291165180139784837"), BigInt("95883416848225362139258911209835805765"))
    runTest(BigInt("205827752893262866676786196803754932273"), BigInt("95883416848225362139258911209835805765"), iv=BigInt("56228932323966364015683275655639818280"))
    runTest(BigInt("199346244042583787226921311728994900020"), BigInt("50818130307682320576899190190350237012"))
    runTest(BigInt("10923853975411504406511121930015167005"), BigInt("50818130307682320576899190190350237012"), iv=BigInt("144087444337191600128043622494219822880"))
    setTopKey(BigInt("74832202448209564523772602416070740798"))
    setKey(BigInt("112026326342838444672188959754976705645"))
    runTest(BigInt("293917419943261046578359329103316378443"), BigInt("89579641818767849568847491553305713229"))
    runTest(BigInt("5840346656808075162198214893104034751"), BigInt("89579641818767849568847491553305713229"), iv=BigInt("66950210164843701505670739786495915338"))
    runTest(BigInt("130035743637871460862209015100940411794"), BigInt("93628251003546449780213578205185209664"))
    runTest(BigInt("204299603662852178847721096832400073780"), BigInt("93628251003546449780213578205185209664"), iv=BigInt("46743133376058891692811048273001733986"))
    runTest(BigInt("330635289187442935761338809772539556873"), BigInt("158398401094928844131844331221603854446"))
    runTest(BigInt("332842336139037983701054224490608544507"), BigInt("158398401094928844131844331221603854446"), iv=BigInt("56286410626811696090290609446032451186"))
    runTest(BigInt("213694921510040089242505919607047579393"), BigInt("125535948645770945484267697006478643038"))
    runTest(BigInt("122156267196134240274942799868731227946"), BigInt("125535948645770945484267697006478643038"), iv=BigInt("158558940569449647633741912958869462074"))
    runTest(BigInt("22496108462831928088234537409054028369"), BigInt("64146652058942842666026363667725769297"))
    runTest(BigInt("169338621728476350472108910526545279518"), BigInt("64146652058942842666026363667725769297"), iv=BigInt("44224644734571921401605116987893643869"))
    runTest(BigInt("243310830017543240931122842580510754526"), BigInt("93218752474974332929496134057898098996"))
    runTest(BigInt("214541431487255736640388440405050354323"), BigInt("93218752474974332929496134057898098996"), iv=BigInt("59115887320505410288901835466430246948"))
    runTest(BigInt("285609811854027326709127625141744931442"), BigInt("157420957871454922386588292678826800933"))
    runTest(BigInt("696540049690611988178752351655265844"), BigInt("157420957871454922386588292678826800933"), iv=BigInt("79055075950222470665949356442624797219"))
    runTest(BigInt("83485869639635680923208793603856965302"), BigInt("90903088857742119665406963027270851386"))
    runTest(BigInt("143244131881890927300377968810698949311"), BigInt("90903088857742119665406963027270851386"), iv=BigInt("104044144827491608271409160000572573755"))
    runTest(BigInt("298881670704136539604501217981062305476"), BigInt("60096947727998829789339728666635232366"))
    runTest(BigInt("197122413209397230638588494934876591289"), BigInt("60096947727998829789339728666635232366"), iv=BigInt("156117752377361010212545226466505479241"))
    runTest(BigInt("122873267629999155289518519338913660500"), BigInt("157297477687461225554602565858831398958"))
    runTest(BigInt("75721060445407382323023070290436939710"), BigInt("157297477687461225554602565858831398958"), iv=BigInt("86603378127941656995505506427966415189"))
    setTopKey(BigInt("98811200996304324272982343149382152550"))
    setKey(BigInt("157353390978756052022255487698594708294"))
    runTest(BigInt("70838026833321259611917979998427013801"), BigInt("106708134577519154891092372131495034417"))
    runTest(BigInt("194179277609087583951658619361729620701"), BigInt("106708134577519154891092372131495034417"), iv=BigInt("102559657057606503480754017796242165293"))
    runTest(BigInt("282325365144022181142548379505715057097"), BigInt("98940357393515322574542942718621801796"))
    runTest(BigInt("34680448480152712344479729062080363440"), BigInt("98940357393515322574542942718621801796"), iv=BigInt("99953549638962419734098719649468071289"))
    runTest(BigInt("110082571344358816989065792336301301643"), BigInt("93671537626363393436667285260239382572"))
    runTest(BigInt("315489047571224884869027868972492985327"), BigInt("93671537626363393436667285260239382572"), iv=BigInt("93358433780559488911928110098378273589"))
    runTest(BigInt("209255143734010720275544590985542601422"), BigInt("101620296584478692828534273875356309539"))
    runTest(BigInt("202284314253564647877221695672484611857"), BigInt("101620296584478692828534273875356309539"), iv=BigInt("162749505702329294055132001298532619349"))
    runTest(BigInt("308912043040729324280226854412672969119"), BigInt("165417210787821484983164567605606493023"))
    runTest(BigInt("272331610783654864031535564315498941628"), BigInt("165417210787821484983164567605606493023"), iv=BigInt("42978878200325793765108552602053790034"))
    runTest(BigInt("281573673362391448813403074730998031365"), BigInt("153070192757871473464108471584687677548"))
    runTest(BigInt("150384422531295507562205539202218146665"), BigInt("153070192757871473464108471584687677548"), iv=BigInt("150665678254439059452521907124622282031"))
    runTest(BigInt("208104748273359989547140786144779704373"), BigInt("92269536500336705443025651588953823558"))
    runTest(BigInt("112957199793142466396381412497322873355"), BigInt("92269536500336705443025651588953823558"), iv=BigInt("52033456706564407176996300208111626532"))
    runTest(BigInt("53268080448656282399008238366188930084"), BigInt("162349743836867448257189664947798164066"))
    runTest(BigInt("152630811608053908148088467535955983836"), BigInt("162349743836867448257189664947798164066"), iv=BigInt("133574193187702756405285517274681210437"))
    runTest(BigInt("11421859428215280025173933463356205474"), BigInt("166773051287841814944234652992336458344"))
    runTest(BigInt("134160955443370903615618804513022390480"), BigInt("166773051287841814944234652992336458344"), iv=BigInt("116215822919348862174282460562732905825"))
    runTest(BigInt("127162886543306014464116584306188948827"), BigInt("127872932159287497765444525045252175207"))
    runTest(BigInt("22860036150493754854802829061217385773"), BigInt("127872932159287497765444525045252175207"), iv=BigInt("164078206764322207830931927656598028386"))
    setTopKey(BigInt("157167203847945773712128117795546681421"))
    setKey(BigInt("85492146111241142647586669949539876134"))
    runTest(BigInt("153627985698097191915325934546500900398"), BigInt("88167397054878596095626863243453873270"))
    runTest(BigInt("287044824129631349393211855981687124142"), BigInt("88167397054878596095626863243453873270"), iv=BigInt("96038801185791467820571822199418813310"))
    runTest(BigInt("195514053763307515178777631719867264716"), BigInt("121179146425842435467425342351429870186"))
    runTest(BigInt("243452627340797113265329184903472652134"), BigInt("121179146425842435467425342351429870186"), iv=BigInt("49354958618413258240941499126127556643"))
    runTest(BigInt("33261166623265215733687240074918431806"), BigInt("166678639775525127519597274160537683506"))
    runTest(BigInt("26769161886774543195110378485920839118"), BigInt("166678639775525127519597274160537683506"), iv=BigInt("55082063231883717657674783862098240054"))
    runTest(BigInt("235620510108113433332446462037700966216"), BigInt("92066185856683498410053074910080104310"))
    runTest(BigInt("249052994079595315376978326148795754601"), BigInt("92066185856683498410053074910080104310"), iv=BigInt("101494584940178699301395026063547903780"))
    runTest(BigInt("188939594655404263780661703320181879217"), BigInt("49557560401751102755136895503235435105"))
    runTest(BigInt("2404049640457573177340342442145117532"), BigInt("49557560401751102755136895503235435105"), iv=BigInt("56202443515320979155040979541341388102"))
    runTest(BigInt("197669774719964662026756947317208985020"), BigInt("45625836125944064538507920409699693177"))
    runTest(BigInt("240009759560611332214658436072857357862"), BigInt("45625836125944064538507920409699693177"), iv=BigInt("60247218901571413446697924710374987864"))
    runTest(BigInt("51170008876282692798153453779471913030"), BigInt("89226163156084432010570128597416244347"))
    runTest(BigInt("243254499429555798039593562009152099322"), BigInt("89226163156084432010570128597416244347"), iv=BigInt("110696997326700976498988626915938957636"))
    runTest(BigInt("325224840228600156028684039703256206523"), BigInt("71006710954859131413288761040421156664"))
    runTest(BigInt("265202161241139459074444811734535969931"), BigInt("71006710954859131413288761040421156664"), iv=BigInt("48180521856564370518882438315772177212"))
    runTest(BigInt("98255993250780592431073034742331855824"), BigInt("118603709036688639505952125370536834133"))
    runTest(BigInt("332244623237500401196571114501443455047"), BigInt("118603709036688639505952125370536834133"), iv=BigInt("122457308393137140244020029802711512880"))
    runTest(BigInt("112683210387222069720070424767136616247"), BigInt("93660810611064499476010521208704673880"))
    runTest(BigInt("299741505246724873293499794533810757724"), BigInt("93660810611064499476010521208704673880"), iv=BigInt("159861416640412691444062050660128939076"))
    setTopKey(BigInt("50949075210718590738702489179914655342"))
    setKey(BigInt("94600995186077011141858798338038592862"))
    runTest(BigInt("271331818373887142621146678459880003675"), BigInt("95908138666130135025988826471616041334"))
    runTest(BigInt("262067139825028628923959151071847884828"), BigInt("95908138666130135025988826471616041334"), iv=BigInt("101589143499582596601994459609907415080"))
    runTest(BigInt("45895798428915617281260163403015169258"), BigInt("151865786536026088603072943859462387835"))
    runTest(BigInt("171634118382173567200629884111611922157"), BigInt("151865786536026088603072943859462387835"), iv=BigInt("118485322112818933475870305032721627994"))
    runTest(BigInt("275735142225014984626538561187091938850"), BigInt("120125069348607653279788574123211060059"))
    runTest(BigInt("189450966853956411599063706758562661485"), BigInt("120125069348607653279788574123211060059"), iv=BigInt("150837588480893704560414990982191402570"))
    runTest(BigInt("257479196513490161276631681924352834894"), BigInt("153491317543629140412870468161340188707"))
    runTest(BigInt("7792624109248228298663112442398867169"), BigInt("153491317543629140412870468161340188707"), iv=BigInt("48342296418238246680724655477047114564"))
    runTest(BigInt("330734402155433990086102562718608247354"), BigInt("44358916170921594181944530799347454557"))
    runTest(BigInt("221570489678906345059243745302843680292"), BigInt("44358916170921594181944530799347454557"), iv=BigInt("78754044827389228109178388278283828009"))
    runTest(BigInt("30827023455380963668899492646268691476"), BigInt("81551603278615078920466516111033975909"))
    runTest(BigInt("61986867193833777333912011097993071491"), BigInt("81551603278615078920466516111033975909"), iv=BigInt("88302051813825644818368534590937975592"))
    runTest(BigInt("27717894701393662774126044109842582965"), BigInt("114890912927693571369249776007029077584"))
    runTest(BigInt("243895401133078911549334398220113425263"), BigInt("114890912927693571369249776007029077584"), iv=BigInt("166419167134528700000090869814246924893"))
    runTest(BigInt("337372492622722473468156408275537483873"), BigInt("89413893798263913392116643672062439026"))
    runTest(BigInt("330000890153687360232910071304117327762"), BigInt("89413893798263913392116643672062439026"), iv=BigInt("133459211654589131103126609103899215732"))
    runTest(BigInt("111167387548915805123808327656739382642"), BigInt("152006628613682264135099273078865412732"))
    runTest(BigInt("79814775777900788842556169524132801336"), BigInt("152006628613682264135099273078865412732"), iv=BigInt("70622159237731561096457315664594150718"))
    runTest(BigInt("243626996706648864738460102951174287123"), BigInt("111936899679624346613868862491416754247"))
    runTest(BigInt("775453122020597498366302212204272944"), BigInt("111936899679624346613868862491416754247"), iv=BigInt("102887642548383878138025368599633213744"))
    setTopKey(BigInt("157380858695625783747977655784521556069"))
    setKey(BigInt("78873350231857238230804654728091616078"))
    runTest(BigInt("111362478971512164391350608589550469093"), BigInt("166689326205748458940429475987454451758"))
    runTest(BigInt("198616715944611407294290573818375453227"), BigInt("166689326205748458940429475987454451758"), iv=BigInt("105478208013139286998788301578489506597"))
    runTest(BigInt("133246368489928436125035290071583962864"), BigInt("42703647894209703416477824670860660032"))
    runTest(BigInt("3314070960640938344648019443879893401"), BigInt("42703647894209703416477824670860660032"), iv=BigInt("65590356681557657709391632894747949372"))
    runTest(BigInt("271632285099585332903426921460692426958"), BigInt("62906165473948935332079906796998974553"))
    runTest(BigInt("6189963478423599135304871638170156086"), BigInt("62906165473948935332079906796998974553"), iv=BigInt("124216256866066516274929737596801340512"))
    runTest(BigInt("105014760253273146471184778891617750695"), BigInt("126599462961748346999077050094938121595"))
    runTest(BigInt("20009958981829520722210234262812839375"), BigInt("126599462961748346999077050094938121595"), iv=BigInt("69412800499645975293328860813922488942"))
    runTest(BigInt("178436213582900002149519660409397513055"), BigInt("44250441248791947237783016838094617904"))
    runTest(BigInt("34780787495947859886495978211011668762"), BigInt("44250441248791947237783016838094617904"), iv=BigInt("126782569152809366121904239956538838613"))
    runTest(BigInt("234562417864062129920311357282836739263"), BigInt("109357446366865232865790808614327626272"))
    runTest(BigInt("221168457760813169370587079711460156392"), BigInt("109357446366865232865790808614327626272"), iv=BigInt("92310627229916326655161385526532514423"))
    runTest(BigInt("88492441309768090191043036632545361717"), BigInt("125505121980659591549270515180107346285"))
    runTest(BigInt("258018313111547761776184616738612907337"), BigInt("125505121980659591549270515180107346285"), iv=BigInt("62853491262688907582927482507802607150"))
    runTest(BigInt("273184363994652033707482243956880226270"), BigInt("120084690179552960777799890274180169085"))
    runTest(BigInt("167378825570580143268750850884538153367"), BigInt("120084690179552960777799890274180169085"), iv=BigInt("98987839776446843977046262994336233284"))
    runTest(BigInt("200248603461405900650708993865450365678"), BigInt("84246115693738988314896008813965954083"))
    runTest(BigInt("246592659822543105610287828382179931134"), BigInt("84246115693738988314896008813965954083"), iv=BigInt("92259233823859631416323813137015259985"))
    runTest(BigInt("182213655880503357566487729617948261808"), BigInt("130894989737369175016902006252404885314"))
    runTest(BigInt("293783715583529786671221657079046995866"), BigInt("130894989737369175016902006252404885314"), iv=BigInt("55051269621265461056759079193387482924"))
    setTopKey(BigInt("114969401582617143627008525082467786018"))
    setKey(BigInt("104013880933092606031930221508194610241"))
    runTest(BigInt("320362860083775224996624499680233934727"), BigInt("47086026226155376961264634458242961227"))
    runTest(BigInt("110385572676249631494169511533406096063"), BigInt("47086026226155376961264634458242961227"), iv=BigInt("162602967201922741568882384463687284292"))
    runTest(BigInt("114581507489506116940643133059618864950"), BigInt("60446008476719506914681527334572472611"))
    runTest(BigInt("290758424995429597768267117444962051103"), BigInt("60446008476719506914681527334572472611"), iv=BigInt("118910418193452181833142975121954385490"))
    runTest(BigInt("118341421398989403871034636637538654145"), BigInt("91920356111829401606798803596048875835"))
    runTest(BigInt("135843390367889373014122271101176456433"), BigInt("91920356111829401606798803596048875835"), iv=BigInt("89329904818785189361249092241658180951"))
    runTest(BigInt("113791017752268642502083486849326092670"), BigInt("124174904823470605243404638230681313618"))
    runTest(BigInt("102803392727950047469047418426540519692"), BigInt("124174904823470605243404638230681313618"), iv=BigInt("93270936734660658991153288559661776187"))
    runTest(BigInt("4363276506128158493202046246381150943"), BigInt("142800721925921350127293191369122920749"))
    runTest(BigInt("174426362125901984440657436751562930751"), BigInt("142800721925921350127293191369122920749"), iv=BigInt("68099127529978333299119781960940791131"))
    runTest(BigInt("112524571845463976413299723509449477861"), BigInt("118858820628910033997520629339246055798"))
    runTest(BigInt("137734059692404902209470285470706043559"), BigInt("118858820628910033997520629339246055798"), iv=BigInt("58902818954960374923343173005965927231"))
    runTest(BigInt("283987520663383118481112147229588632161"), BigInt("102596264501191984899634536994115958895"))
    runTest(BigInt("335569404241265542432707736865842031881"), BigInt("102596264501191984899634536994115958895"), iv=BigInt("130738894012627029164006895089834876193"))
    runTest(BigInt("191596199502375366498955455451469100990"), BigInt("62703967691948715556474241276597137214"))
    runTest(BigInt("98421449255619542929911933143717145219"), BigInt("62703967691948715556474241276597137214"), iv=BigInt("65305554398994645014365520283204343406"))
    runTest(BigInt("326590518848324947321606554827051682816"), BigInt("53612158360288389690242462912936180815"))
    runTest(BigInt("32552865687747613580786220979833870148"), BigInt("53612158360288389690242462912936180815"), iv=BigInt("146860495633886258581364951421793747819"))
    runTest(BigInt("226645705833400391694897058866803660941"), BigInt("141221762345617887478344820977333122642"))
    runTest(BigInt("184827811266128276355379886981373304596"), BigInt("141221762345617887478344820977333122642"), iv=BigInt("106614312446084765975074082631127945035"))
    setTopKey(BigInt("69376193854122020586118300587504918599"))
    setKey(BigInt("121361000174459387957177477285359474269"))
    runTest(BigInt("28983222431246713789406481497711766568"), BigInt("166648293718764212611707929418537525856"))
    runTest(BigInt("115492517319764740427558508418336410837"), BigInt("166648293718764212611707929418537525856"), iv=BigInt("55056464226351530461436053407147961699"))
    runTest(BigInt("235003590054147093832611355517386964530"), BigInt("162739370116009118685678305110557271915"))
    runTest(BigInt("224181255477012148029233510341059000784"), BigInt("162739370116009118685678305110557271915"), iv=BigInt("121206204515493513849398423981849072992"))
    runTest(BigInt("26227372303447607733218916883782501239"), BigInt("168138036712826398982752820765377963863"))
    runTest(BigInt("3146519676561694316799375107164872800"), BigInt("168138036712826398982752820765377963863"), iv=BigInt("150370401768984129656257505056581708901"))
    runTest(BigInt("153032296594387515003540485615267634107"), BigInt("165235398941237192193630601644291147554"))
    runTest(BigInt("186892352494779381199582317840551515020"), BigInt("165235398941237192193630601644291147554"), iv=BigInt("63056558921539618442670393338976950053"))
    runTest(BigInt("59659489411003188079252158103104556824"), BigInt("112243527802996274839213015836405163093"))
    runTest(BigInt("329918737078803985887162472357977706078"), BigInt("112243527802996274839213015836405163093"), iv=BigInt("115836399729598284474134489702620219512"))
    runTest(BigInt("211753883239189993046576037725022280799"), BigInt("109274122131846631429114642868438314792"))
    runTest(BigInt("177286265263541843692032291505045679465"), BigInt("109274122131846631429114642868438314792"), iv=BigInt("141154197180413673210968691028648032815"))
    runTest(BigInt("195651218700244614157941362411171306152"), BigInt("52079572098290150879090614312801807967"))
    runTest(BigInt("119704350662268510808982069947112927832"), BigInt("52079572098290150879090614312801807967"), iv=BigInt("109392800762674530768562827331113858402"))
    runTest(BigInt("339099290675100662342847156132013327113"), BigInt("77564237620954153911257671639090489171"))
    runTest(BigInt("86604524300144590036846213016079209443"), BigInt("77564237620954153911257671639090489171"), iv=BigInt("113153015450604646939775280530788661624"))
    runTest(BigInt("10655375322981391887434927432666679267"), BigInt("166534509862945219807226108572342511923"))
    runTest(BigInt("27161177603121025024370601542996917724"), BigInt("166534509862945219807226108572342511923"), iv=BigInt("109580569758260043133076177460743858220"))
    runTest(BigInt("11775987405355894530171961111398699229"), BigInt("141543538318019616872116672253882345039"))
    runTest(BigInt("285653786501492610967091426251360028751"), BigInt("141543538318019616872116672253882345039"), iv=BigInt("100311630107264055904783492270974784576"))
    setTopKey(BigInt("48185231413743007685908816522865425450"))
    setKey(BigInt("49633859191159772730451052139292481640"))
    runTest(BigInt("186253063132567846232961746158976396525"), BigInt("110944827529522557782611231876509621844"))
    runTest(BigInt("107266316898137138209116146736444160121"), BigInt("110944827529522557782611231876509621844"), iv=BigInt("54904208464409295695425009135526241339"))
    runTest(BigInt("267983466057291194537722720983730836240"), BigInt("93489483513758920387956524559757878611"))
    runTest(BigInt("150586353893793214020383615521090914222"), BigInt("93489483513758920387956524559757878611"), iv=BigInt("102849794689100516439899391405275366713"))
    runTest(BigInt("274918931782964705844924907441872442581"), BigInt("43150342201092159628989622995170261537"))
    runTest(BigInt("87182464836953588078707816851502853034"), BigInt("43150342201092159628989622995170261537"), iv=BigInt("81531338302200453154969509082767322454"))
    runTest(BigInt("78457742766623278540183265682108267189"), BigInt("122831411479865677615144728022650463779"))
    runTest(BigInt("54914497063680325822746554325327419655"), BigInt("122831411479865677615144728022650463779"), iv=BigInt("150790636422439623738631785777763338791"))
    runTest(BigInt("315742229929411433095104497373755215651"), BigInt("61664659773676044066756478179545084730"))
    runTest(BigInt("248230803003057590018928695224350752543"), BigInt("61664659773676044066756478179545084730"), iv=BigInt("113365840452886247827454068877834800995"))
    runTest(BigInt("194173334591242174153513329627489154977"), BigInt("132067396178426780808059402616648266860"))
    runTest(BigInt("186166778236758818537975207063322019619"), BigInt("132067396178426780808059402616648266860"), iv=BigInt("165179846704532203742631074433804099182"))
    runTest(BigInt("126913345282279905506706175704374199158"), BigInt("157504901546877169296193881344309930572"))
    runTest(BigInt("173243993938256789391421632912261127618"), BigInt("157504901546877169296193881344309930572"), iv=BigInt("105196673167414376830042897448942460979"))
    runTest(BigInt("2127022321083420869292121500763720366"), BigInt("122550851498271495727837563698666498354"))
    runTest(BigInt("322695824641582987969742747718318703218"), BigInt("122550851498271495727837563698666498354"), iv=BigInt("89433323333416295426354345028429441622"))
    runTest(BigInt("14249702056453406386688473604559973922"), BigInt("93302863062523284598487441647775669086"))
    runTest(BigInt("286258151371014082710729033004986273183"), BigInt("93302863062523284598487441647775669086"), iv=BigInt("141485797298599944929194498946886431018"))
    runTest(BigInt("127051097641745115744722173863026333642"), BigInt("118868271509113620427731420607847087450"))
    runTest(BigInt("249566445662458325225186295071388240694"), BigInt("118868271509113620427731420607847087450"), iv=BigInt("96183062919180592585989953640982792511"))
    setTopKey(BigInt("110883370160673698367641637136919196986"))
    setKey(BigInt("44182844098347977141492636718617222239"))
    runTest(BigInt("305968993233990511451814406479673023761"), BigInt("64349073215727862993383990808065039144"))
    runTest(BigInt("332824268519077573565710031067273953089"), BigInt("64349073215727862993383990808065039144"), iv=BigInt("90945642294825517505748038050355702561"))
    runTest(BigInt("276043077913928590347606273628185268974"), BigInt("81473190451800889352674467888798124621"))
    runTest(BigInt("321059605479749583321606884547851459544"), BigInt("81473190451800889352674467888798124621"), iv=BigInt("133231139920957937381071039218720333601"))
    runTest(BigInt("227375312797949195378285107978645167189"), BigInt("166809760228661942266282466109161633584"))
    runTest(BigInt("262839930768588643237063360528679178768"), BigInt("166809760228661942266282466109161633584"), iv=BigInt("124217252374176546808754508134356316527"))
    runTest(BigInt("270253835598101756480483251371754699534"), BigInt("129128212233298468390074958855996143695"))
    runTest(BigInt("242473112131866340473495226499295941343"), BigInt("129128212233298468390074958855996143695"), iv=BigInt("98998140556621002830498482551346640959"))
    runTest(BigInt("72433160693083521467847607434981431274"), BigInt("120239346072361280165117705207479284848"))
    runTest(BigInt("164628183102180082517519456605842679110"), BigInt("120239346072361280165117705207479284848"), iv=BigInt("109299008005976922822884289689566264632"))
    runTest(BigInt("117906947411666594222036138621126912631"), BigInt("158766669916188796426029964543891416869"))
    runTest(BigInt("216819816605975387150250176421767123157"), BigInt("158766669916188796426029964543891416869"), iv=BigInt("127851961822959946054661909375917435256"))
    runTest(BigInt("133184944771525681414843092623024130000"), BigInt("155687074670613969786691564391317586244"))
    runTest(BigInt("254033050636531856058063245428592300256"), BigInt("155687074670613969786691564391317586244"), iv=BigInt("64452716708535860250303241398656719395"))
    runTest(BigInt("252605713371098466312487101849953238790"), BigInt("109496381924844864612454394966356945238"))
    runTest(BigInt("89250573158404967076586671015970966575"), BigInt("109496381924844864612454394966356945238"), iv=BigInt("69406754436985753371452196851260619837"))
    runTest(BigInt("261611863188474004494169834631893935428"), BigInt("114622436262870187152693287936883515487"))
    runTest(BigInt("318779100357607507616203093839581327091"), BigInt("114622436262870187152693287936883515487"), iv=BigInt("121148987597147232255467855303468993101"))
    runTest(BigInt("22552384467018413190934194615629153656"), BigInt("158829347338839838279066168400878193504"))
    runTest(BigInt("83809603151583270248503396713782478454"), BigInt("158829347338839838279066168400878193504"), iv=BigInt("113256917421561847041689499199445497719"))

}
