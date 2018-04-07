/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018 DaPorkchop_
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.depmanager.signer;

import net.daporkchop.lib.crypto.key.ec.impl.ECDSAKeyPair;
import net.daporkchop.lib.crypto.keygen.ec.ECDSAKeyGen;
import net.daporkchop.lib.crypto.sig.HashTypes;
import net.daporkchop.lib.crypto.sig.ec.ECCurves;
import net.daporkchop.lib.crypto.sig.ec.impl.ECDSAHelper;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.hash.Base58WithHeaders;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author DaPorkchop_
 */
public class SignerMain {
    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        switch (scanner.nextInt()) {
            case 0:
                ECDSAKeyPair pair = ECDSAKeyGen.gen(ECCurves.brainpoolp512t1);
                System.out.println("Public key: " + Base58WithHeaders.encode((byte) 0, "PubKey", pair.encodePublic()));
                System.out.println("Private key: " + Base58WithHeaders.encode((byte) 0, "PrivKey", pair.encodePrivate()));
                break;
            case 1:
                scanner.nextLine();
                String key = scanner.nextLine().trim();
                Base58WithHeaders.Decoded decoded = Base58WithHeaders.decode(key);
                pair = ECDSAKeyPair.decodePrivate(decoded.content);
                String s = scanner.nextLine().trim();
                System.out.println("Signing \"" + s + "\"...");
                System.out.println("Signature: " + Base58.encodeBase58(new ECDSAHelper(HashTypes.SHA_512).sign(s.getBytes(Charset.forName("UTF-8")), pair)));
                break;
        }
        scanner.close();
    }
}
